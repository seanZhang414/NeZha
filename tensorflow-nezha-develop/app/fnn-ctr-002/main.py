# !usr/bin/env python
# -*- coding:utf-8 -*-
# encoding: utf-8

import shutil
import os
import json
import glob
from datetime import date, timedelta
from time import time
import random
import tensorflow as tf
import ast


def input_fn(filenames, batch_size=100, num_epochs=1, perform_shuffle=False, num_parallel_calls=10, prefetch=500000):
    print('Parsing', filenames)

    def decode_libsvm(line):
        # columns = tf.decode_csv(value, record_defaults=CSV_COLUMN_DEFAULTS)
        # features = dict(zip(CSV_COLUMNS, columns))
        # labels = features.pop(LABEL_COLUMN)
        fields = tf.string_split([line], ',')
        labels = tf.string_to_number(fields.values[0], out_type=tf.float32)
        feat_vals = tf.string_to_number(fields.values[1:], out_type=tf.float32)
        return {"feat_vals": feat_vals}, labels

    # Extract lines from input files using the Dataset API, can pass one filename or filename list
    dataset = tf.data.TextLineDataset(filenames).map(decode_libsvm, num_parallel_calls=num_parallel_calls).prefetch(
        prefetch)  # multi-thread pre-process then prefetch
    # Randomizes input using a window of 256 elements (read into memory)
    if perform_shuffle:
        dataset = dataset.shuffle(buffer_size=256)

    # epochs from blending together.
    dataset = dataset.repeat(num_epochs)
    dataset = dataset.batch(batch_size)  # Batch size to use

    # return dataset.make_one_shot_iterator()
    iterator = dataset.make_one_shot_iterator()
    iterator.get_next()
    batch_features, batch_labels = iterator.get_next()
    # return tf.reshape(batch_ids,shape=[-1,field_size]), tf.reshape(batch_vals,shape=[-1,field_size]), batch_labels
    return batch_features, batch_labels


def model_fn(features, labels, mode, params):
    """Bulid Model function f(x) for Estimator."""
    # ------hyperparameters----
    field_size = params["field_size"]
    embedding_size = params["embedding_size"]
    batch_norm = params["batch_norm"]
    optimizer_p = params["optimizer"]
    l2_reg = params["l2_reg"]
    batch_norm_decay = params["batch_norm_decay"]
    learning_rate = params["learning_rate"]

    # batch_norm_decay = params["batch_norm_decay"]
    # optimizer = params["optimizer"]
    layers = map(int, params["deep_layers"].split(','))
    dropout = map(float, params["dropout"].split(','))

    # ------bulid weights------
    FNN_B = tf.get_variable(name='fnn_bias', shape=[1], initializer=tf.constant_initializer(0.0))

    # ------build feaure-------

    feat_vals = features['feat_vals']
    fm_bias = tf.reshape(feat_vals[:, 0], shape=[-1, 1])
    fm_w = tf.reshape(feat_vals[:, 1:field_size + 1], shape=[-1, field_size])
    fm_v = tf.reshape(feat_vals[:, field_size + 1:], shape=[-1, field_size * embedding_size])

    label = features['feat_vals']
    '''
    # ------build f(x)------
    with tf.variable_scope("First-order"):
        y_w = tf.reduce_sum(fm_w, 1)
        y_w = tf.reshape(y_w, shape=[-1, 1])
    with tf.variable_scope("Second-order"):
        fm_v2 = tf.reshape(fm_v, shape=[-1, field_size, embedding_size])
        embeddings = tf.transpose(fm_v2, perm=[0, 2, 1])
        sum_square = tf.square(tf.reduce_sum(embeddings, 1))
        square_sum = tf.reduce_sum(tf.square(embeddings), 1)
        y_v = 0.5 * tf.reduce_sum(tf.subtract(sum_square, square_sum), 1)  # None * 1
        y_v = tf.reshape(y_v, shape=[-1, 1])
    '''

    with tf.variable_scope("Deep-part"):
        if batch_norm:
            # normalizer_fn = tf.contrib.layers.batch_norm
            # normalizer_fn = tf.layers.batch_normalization
            if mode == tf.estimator.ModeKeys.TRAIN:
                train_phase = True
                # normalizer_params = {'decay': batch_norm_decay, 'center': True, 'scale': True, 'updates_collections': None, 'is_training': True, 'reuse': None}
            else:
                train_phase = False
                # normalizer_params = {'decay': batch_norm_decay, 'center': True, 'scale': True, 'updates_collections': None, 'is_training': False, 'reuse': True}
        else:
            normalizer_fn = None
            normalizer_params = None
        deep_inputs = tf.reshape(feat_vals, shape=[-1, field_size * (embedding_size + 1) + 1])
        for i in range(len(layers)):
            # if FLAGS.batch_norm:
            #    deep_inputs = batch_norm_layer(deep_inputs, train_phase=train_phase, scope_bn='bn_%d' %i)
            # normalizer_params.update({'scope': 'bn_%d' %i})
            deep_inputs = tf.contrib.layers.fully_connected(inputs=deep_inputs, num_outputs=layers[i], \
                                                            # normalizer_fn=normalizer_fn, normalizer_params=normalizer_params, \
                                                            weights_regularizer=tf.contrib.layers.l2_regularizer(
                                                                l2_reg), scope='mlp%d' % i)
            if batch_norm:
                deep_inputs = batch_norm_layer(deep_inputs, train_phase=train_phase, scope_bn='bn_%d' % i,
                                               batch_norm_decay=batch_norm_decay)  # 放在RELU之后 https://github.com/ducha-aiki/caffenet-benchmark/blob/master/batchnorm.md#bn----before-or-after-relu
            if mode == tf.estimator.ModeKeys.TRAIN:
                deep_inputs = tf.nn.dropout(deep_inputs, keep_prob=dropout[
                    i])  # Apply Dropout after all BN layers and set dropout=0.8(drop_ratio=0.2)

        y_deep = tf.contrib.layers.fully_connected(inputs=deep_inputs, num_outputs=1, activation_fn=tf.identity, \
                                                   weights_regularizer=tf.contrib.layers.l2_regularizer(l2_reg),
                                                   scope='deep_out')
        y_d = tf.reshape(y_deep, shape=[-1, 1])
        # sig_wgts = tf.get_variable(name='sigmoid_weights', shape=[layers[-1]], initializer=tf.glorot_normal_initializer())
        # sig_bias = tf.get_variable(name='sigmoid_bias', shape=[1], initializer=tf.constant_initializer(0.0))
        # deep_out = tf.nn.xw_plus_b(deep_inputs,sig_wgts,sig_bias,name='deep_out')

    with tf.variable_scope("DeepFM-out"):
        # y_bias = FM_B * tf.ones_like(labels, dtype=tf.float32)  # None * 1  warning;这里不能用label，否则调用predict/export函数会出错，train/evaluate正常；初步判断estimator做了优化，用不到label时不传
        y_bias = FNN_B * tf.ones_like(fm_bias, dtype=tf.float32)  # None * 1
        y_b = tf.reshape(y_bias, shape=[-1, 1])
        y = y_b + y_d
        y = tf.reshape(y, shape=[-1])
        pred = tf.sigmoid(y)

    predictions = {"prob": pred}
    export_outputs = {
        tf.saved_model.signature_constants.DEFAULT_SERVING_SIGNATURE_DEF_KEY: tf.estimator.export.PredictOutput(
            predictions)}
    # Provide an estimator spec for `ModeKeys.PREDICT`
    if mode == tf.estimator.ModeKeys.PREDICT:
        return tf.estimator.EstimatorSpec(
            mode=mode,
            predictions=predictions,
            export_outputs=export_outputs)

    # ------bulid loss------
    loss = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=y, labels=labels)) + \
           l2_reg * tf.nn.l2_loss(FNN_B)

    # Provide an estimator spec for `ModeKeys.EVAL`
    eval_metric_ops = {
        "auc": tf.metrics.auc(labels, pred)
    }
    if mode == tf.estimator.ModeKeys.EVAL:
        return tf.estimator.EstimatorSpec(
            mode=mode,
            predictions=predictions,
            loss=loss,
            eval_metric_ops=eval_metric_ops)

    # ------bulid optimizer------
    optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate, beta1=0.9, beta2=0.999, epsilon=1e-8)
    if optimizer_p == 'Adam':
        optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate, beta1=0.9, beta2=0.999, epsilon=1e-8)
    elif optimizer_p == 'Adagrad':
        optimizer = tf.train.AdagradOptimizer(learning_rate=learning_rate, initial_accumulator_value=1e-8)
    elif optimizer_p == 'Momentum':
        optimizer = tf.train.MomentumOptimizer(learning_rate=learning_rate, momentum=0.95)
    elif optimizer_p == 'ftrl':
        optimizer = tf.train.FtrlOptimizer(learning_rate)

    train_op = optimizer.minimize(loss, global_step=tf.train.get_global_step())

    # Provide an estimator spec for `ModeKeys.TRAIN` modes
    if mode == tf.estimator.ModeKeys.TRAIN:
        return tf.estimator.EstimatorSpec(
            mode=mode,
            predictions=predictions,
            loss=loss,
            train_op=train_op)

        # Provide an estimator spec for `ModeKeys.EVAL` and `ModeKeys.TRAIN` modes.
        # return tf.estimator.EstimatorSpec(
        #        mode=mode,
        #        loss=loss,
        #        train_op=train_op,
        #        predictions={"prob": pred},
        #        eval_metric_ops=eval_metric_ops)


def batch_norm_layer(x, train_phase, scope_bn, batch_norm_decay):
    bn_train = tf.contrib.layers.batch_norm(x, decay=batch_norm_decay, center=True, scale=True,
                                            updates_collections=None, is_training=True, reuse=None, scope=scope_bn)
    bn_infer = tf.contrib.layers.batch_norm(x, decay=batch_norm_decay, center=True, scale=True,
                                            updates_collections=None, is_training=False, reuse=True, scope=scope_bn)
    z = tf.cond(tf.cast(train_phase, tf.bool), lambda: bn_train, lambda: bn_infer)
    return z


tf.app.flags.DEFINE_string("task_type", "train", "task_type train_export_pred")
FLAGS = tf.app.flags.FLAGS


def main(_):
    # input flags
    # tf.app.flags.DEFINE_string("task_type", "train", "task_type")
    # tf.app.flags.DEFINE_integer("epochs", 1, "number of epochs")
    # tf.app.flags.DEFINE_integer("batch_size", 100, "batch size")
    # tf.app.flags.DEFINE_float("lr", 0.0005, "learning rate")
    # FLAGS = tf.app.flags.FLAGS

    task_type = FLAGS.task_type
    # task_type="train"
    print task_type
    print('TF_CONFIG', os.environ.get('TF_CONFIG'))

    # task_type = FLAGS.task_type
    # print task_type
    # ------input&&output path set------
    sample_name = "mid_ftrl_fm_ctr_v007"
    model_name = "fnn-ctr-002"
    dt = (date.today() + timedelta(-1)).strftime('%Y%m%d')
    # data_dir = "/data/sample/" + sample_name + ".sample"
    data_dir = "/data/sample/" + sample_name
    model_dir = "/data/model/" + model_name
    pred_dir = "/data/sample/" + sample_name + ".sample"
    servable_model_dir = "/data/serving-model/" + model_name
    logdir = "/data/logs/" + model_name

    # 从环境变量TF_CONFIG中读取json格式的数据
    tf_config_json = os.environ.get("TF_CONFIG", "{}")

    # 反序列化成python对象
    tf_config = json.loads(tf_config_json)

    # 获取角色类型和id， 比如这里的job_name 是 "worker" and task_id 是 0
    task = tf_config.get("task", {})
    job_name = task["type"]
    if job_name == "worker":
        task_id = task["index"]
        task_id_ = '0' + str(task_id)
        tr_files = data_dir + '/train/' + task_id_
        print("tr_files:", tr_files)
        # va_files = data_dir+"/va"
        va_files = data_dir + '/train/04'
        print("va_files:", va_files)
        te_files = data_dir + '/train/04'
        print("te_files:", te_files)
    else:
        tr_files = data_dir + '/train/04'
        print("tr_files:", tr_files)
        # va_files = data_dir+"/va"
        va_files = data_dir + '/train/04'
        print("va_files:", va_files)
        te_files = data_dir + '/train/tmp'
        print("te_files:", te_files)

    #
    clear_existing_model = False  # True
    if clear_existing_model:
        try:
            shutil.rmtree(model_dir)
        except Exception as e:
            print(e, "at clear_existing_model")
        else:
            print("existing model cleaned at %s" % model_dir)

    #

    learning_rate_ = 0.0001  # FLAGS.lr
    epochs_ = 3
    batch_size_ = 100  # FLAGS.batch_size

    field_size_ = 43
    embedding_size_ = 6
    feature_size_ = field_size_ * (embedding_size_ + 1) + 1

    # ------bulid Tasks------
    model_params = {
        "field_size": field_size_,
        "embedding_size": embedding_size_,
        "learning_rate": learning_rate_,
        "batch_norm_decay": 0.9,
        "batch_size": batch_size_,
        "l2_reg": 0.00001,
        "batch_norm": False,
        "optimizer": "adam",
        "batch_norm_decay": 0.9,
        "deep_layers": '256,128,64',
        "dropout": '0.95,0.95,0.95'
    }
    # config = tf.estimator.RunConfig(save_checkpoints_steps=1000,keep_checkpoint_max=20,
    #           log_step_count_steps=1000,save_summary_steps=1000)
    config = tf.estimator.RunConfig(keep_checkpoint_max=20,
                                    log_step_count_steps=1000, save_summary_steps=1000)
    FNN = tf.estimator.Estimator(model_fn=model_fn, model_dir=model_dir, params=model_params, config=config)

    if task_type == 'train':
        train_spec = tf.estimator.TrainSpec(
            input_fn=lambda: input_fn(tr_files, num_epochs=epochs_, batch_size=batch_size_))
        eval_spec = tf.estimator.EvalSpec(input_fn=lambda: input_fn(va_files, num_epochs=1, batch_size=batch_size_),
                                          steps=None, start_delay_secs=30, throttle_secs=30)
        tf.estimator.train_and_evaluate(FNN, train_spec, eval_spec)
    elif task_type == 'eval':
        FNN.evaluate(input_fn=lambda: input_fn(va_files, num_epochs=1, batch_size=batch_size_))
    elif task_type == 'infer':
        preds = FNN.predict(input_fn=lambda: input_fn(te_files, num_epochs=1, batch_size=batch_size_),
                            predict_keys="prob")
        with open(pred_dir, "w") as fo:
            for prob in preds:
                fo.write("%f\n" % (prob['prob']))
    elif task_type == 'export':

        feature_spec = {
            'feat_vals': tf.placeholder(dtype=tf.float32, shape=[None, feature_size_], name='feat_vals')
        }
        serving_input_receiver_fn = tf.estimator.export.build_raw_serving_input_receiver_fn(feature_spec)
        FNN.export_savedmodel(servable_model_dir, serving_input_receiver_fn)


if __name__ == "__main__":
    print("Start")
    tf.logging.set_verbosity(tf.logging.INFO)
    tf.app.run()