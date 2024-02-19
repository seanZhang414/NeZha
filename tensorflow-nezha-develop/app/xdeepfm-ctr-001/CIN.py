#!usr/bin/env python
# -*- coding:utf-8 -*-
'''
@author:houyawei
@file:CIN.py
@time:2018/10/08
'''

import shutil
import os
import json
import glob
from datetime import date, timedelta
from time import time
import random
import tensorflow as tf
import ast
import numpy as np


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


"""
    CIN
"""


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
    cross_layer_sizes = params["cross_layer_sizes"]
    # batch_norm_decay = params["batch_norm_decay"]
    # optimizer = params["optimizer"]
    layers = map(int, params["deep_layers"].split(','))
    dropout = map(float, params["dropout"].split(','))
    cross_layers = params["cross_layers"]

    res = False
    direct = False
    bias = False
    reduce_D = False
    f_dim = 2

    # ------bulid weights------
    FNN_B = tf.get_variable(name='concat_bias', shape=[1], initializer=tf.constant_initializer(0.0))

    # print("Init cross part")
    #
    # Cross_B = tf.get_variable(name='cross_b', shape=[cross_layers, field_size * (embedding_size + 1) + 1],
    #                           initializer=tf.glorot_normal_initializer(), trainable=True)
    #
    # Cross_W = tf.get_variable(name='cross_w', shape=[cross_layers, field_size * (embedding_size + 1) + 1],
    #                           initializer=tf.glorot_normal_initializer(), trainable=True)
    #
    # print("Init cross part finished")


    # ------build feaure-------
    feat_vals = features['feat_vals']
    fm_bias = tf.reshape(feat_vals[:, 0], shape=[-1, 1])
    fm_w = tf.reshape(feat_vals[:, 1:field_size + 1], shape=[-1, field_size])
    fm_v = tf.reshape(feat_vals[:, field_size + 1:], shape=[-1, field_size * embedding_size])

    # ------build f(x)------

    """
        embeddings
    """
    fm_m_v = tf.reshape(feat_vals[:, 1:], shape=[-1, field_size * (embedding_size + 1)])
    embeddings = tf.reshape(fm_m_v, shape=[-1, field_size, embedding_size + 1])

    print "feat:", feat_vals[:, 1:].shape
    print "embeddings: ", embeddings.shape

    # def _build_extreme_FM(self, field_size,embedding_size,embeddings, res=False, direct=False, bias=False, reduce_D=False, f_dim=2):
    nn_input = embeddings
    hidden_nn_layers = []
    field_nums = []
    final_len = 0
    field_num = field_size
    dim = embedding_size + 1
    nn_input = tf.reshape(nn_input, shape=[-1, int(field_num), dim])
    field_nums.append(int(field_num))
    hidden_nn_layers.append(nn_input)
    final_result = []
    split_tensor0 = tf.split(hidden_nn_layers[0], dim * [1], 2)
    with tf.variable_scope("exfm_part") as scope:
        for idx, layer_size in enumerate(cross_layer_sizes):
            split_tensor = tf.split(hidden_nn_layers[-1], dim * [1], 2)
            dot_result_m = tf.matmul(split_tensor0, split_tensor, transpose_b=True)
            dot_result_o = tf.reshape(dot_result_m, shape=[dim, -1, field_nums[0] * field_nums[-1]])
            dot_result = tf.transpose(dot_result_o, perm=[1, 0, 2])

            if reduce_D:
                hparams.logger.info("reduce_D")
                filters0 = tf.get_variable("f0_" + str(idx),
                                           shape=[1, layer_size, field_nums[0], f_dim],
                                           dtype=tf.float32)
                filters_ = tf.get_variable("f__" + str(idx),
                                           shape=[1, layer_size, f_dim, field_nums[-1]],
                                           dtype=tf.float32)
                filters_m = tf.matmul(filters0, filters_)
                filters_o = tf.reshape(filters_m, shape=[1, layer_size, field_nums[0] * field_nums[-1]])
                filters = tf.transpose(filters_o, perm=[0, 2, 1])
            else:
                filters = tf.get_variable(name="f_" + str(idx),
                                          shape=[1, field_nums[-1] * field_nums[0], layer_size],
                                          dtype=tf.float32)
            # dot_result = tf.transpose(dot_result, perm=[0, 2, 1])
            curr_out = tf.nn.conv1d(dot_result, filters=filters, stride=1, padding='VALID')

            # BIAS ADD
            if bias:
                hparams.logger.info("bias")
                b = tf.get_variable(name="f_b" + str(idx),
                                    shape=[layer_size],
                                    dtype=tf.float32,
                                    initializer=tf.zeros_initializer())
                curr_out = tf.nn.bias_add(curr_out, b)

            curr_out = tf.identity(curr_out)

            curr_out = tf.transpose(curr_out, perm=[0, 2, 1])

            if direct:
                hparams.logger.info("all direct connect")
                direct_connect = curr_out
                next_hidden = curr_out
                final_len += layer_size
                field_nums.append(int(layer_size))

            else:
                #                 hparams.logger.info("split connect")
                if idx != len(cross_layer_sizes) - 1:
                    next_hidden, direct_connect = tf.split(curr_out, 2 * [int(layer_size / 2)], 1)
                    final_len += int(layer_size / 2)
                else:
                    direct_connect = curr_out
                    next_hidden = 0
                    final_len += layer_size
                field_nums.append(int(layer_size / 2))

            final_result.append(direct_connect)
            hidden_nn_layers.append(next_hidden)

        # self.cross_params.append(filters)
        #             self.layer_params.append(filters)

        result = tf.concat(final_result, axis=1)
        result = tf.reduce_sum(result, -1)
        if res:
            #             hparams.logger.info("residual network")
            w_nn_output1 = tf.get_variable(name='w_nn_output1',
                                           shape=[final_len, 128],
                                           dtype=tf.float32)
            b_nn_output1 = tf.get_variable(name='b_nn_output1',
                                           shape=[128],
                                           dtype=tf.float32,
                                           initializer=tf.zeros_initializer())
            # self.layer_params.append(w_nn_output1)
            # self.layer_params.append(b_nn_output1)
            exFM_out0 = tf.nn.xw_plus_b(result, w_nn_output1, b_nn_output1)
            exFM_out1 = self._active_layer(logit=exFM_out0,
                                           scope=scope,
                                           activation="relu",
                                           layer_idx=0)
            w_nn_output2 = tf.get_variable(name='w_nn_output2',
                                           shape=[128 + final_len, 1],
                                           dtype=tf.float32)
            b_nn_output2 = tf.get_variable(name='b_nn_output2',
                                           shape=[1],
                                           dtype=tf.float32,
                                           initializer=tf.zeros_initializer())
            #             self.layer_params.append(w_nn_output2)
            #             self.layer_params.append(b_nn_output2)
            exFM_in = tf.concat([exFM_out1, result], axis=1, name="user_emb")
            exFM_out = tf.nn.xw_plus_b(exFM_in, w_nn_output2, b_nn_output2)

        else:
            #             hparams.logger.info("no residual network")
            w_nn_output = tf.get_variable(name='w_nn_output',
                                          shape=[final_len, 1],
                                          dtype=tf.float32)
            b_nn_output = tf.get_variable(name='b_nn_output',
                                          shape=[1],
                                          dtype=tf.float32,
                                          initializer=tf.zeros_initializer())
            #             self.layer_params.append(w_nn_output)
            #             self.layer_params.append(b_nn_output)
            exFM_out = tf.nn.xw_plus_b(result, w_nn_output, b_nn_output)

    with tf.variable_scope("cin-out"):

        y = tf.contrib.layers.fully_connected(inputs=exFM_out, num_outputs=1, activation_fn=tf.identity,
                                              weights_regularizer=tf.contrib.layers.l2_regularizer(l2_reg),
                                              scope='out_layer')
        y_bias = FNN_B * tf.ones_like(fm_bias, dtype=tf.float32)  # None * 1
        y_b = tf.reshape(y_bias, shape=[-1, 1])
        y = y + y_b
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
    loss = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=y, labels=labels)) + l2_reg * tf.nn.l2_loss(
        FNN_B)

    # Provide an estimator spec for `ModeKeys.EVAL`
    eval_metric_ops = {
        "auc": tf.metrics.auc(labels, pred)}

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

    # # 从环境变量TF_CONFIG中读取json格式的数据
    # tf_config_json = os.environ.get("TF_CONFIG", "{}")
    #
    # # 反序列化成python对象
    # tf_config = json.loads(tf_config_json)
    #
    # # 获取角色类型和id， 比如这里的job_name 是 "worker" and task_id 是 0
    # task = tf_config.get("task", {})
    # job_name = task["type"]
    # task_id = task["index"]

    # task_type = FLAGS.task_type
    # print task_type
    # ------input&&output path set------
    sample_name = "mid_ftrl_fm_ctr_v007"
    model_name = "cin-ctr-002"
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
    # task = tf_config.get("task", {})
    # job_name = task["type"]
    # if job_name == "worker":
    #     task_id = task["index"]
    #     task_id_ = '0' + str(task_id)
    #     tr_files = data_dir + '/train/' + task_id_
    #     print("tr_files:", tr_files)
    #     # va_files = data_dir+"/va"
    #     va_files = data_dir + '/train/04'
    #     print("va_files:", va_files)
    #     te_files = data_dir + '/train/04'
    #     print("te_files:", te_files)
    # else:
    #     tr_files = data_dir + '/train/04'
    #     print("tr_files:", tr_files)
    #     # va_files = data_dir+"/va"
    #     va_files = data_dir + '/train/04'
    #     print("va_files:", va_files)
    #     te_files = data_dir + '/train/04'
    #     print("te_files:", te_files)
    tr_files = data_dir + '/train/01'
    print("tr_files:", tr_files)
    # va_files = data_dir+"/va"
    va_files = data_dir + '/train/04'
    print("va_files:", va_files)
    te_files = data_dir + '/train/04'
    print("te_files:", te_files)
    clear_existing_model = True  # True
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

    field_size_ = 33
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
        "cross_layer_sizes": [100, 100, 50],
        "cross_layers": 3,
        "dropout": '0.95,0.95,0.95'
    }
    config = tf.estimator.RunConfig(keep_checkpoint_max=20,
                                    log_step_count_steps=1000, save_summary_steps=1000)
    FNN = tf.estimator.Estimator(model_fn=model_fn, model_dir=model_dir, params=model_params, config=config)

    if task_type == 'train':
        train_spec = tf.estimator.TrainSpec(
            input_fn=lambda: input_fn(tr_files, num_epochs=epochs_, batch_size=batch_size_))
        eval_spec = tf.estimator.EvalSpec(input_fn=lambda: input_fn(va_files, num_epochs=1, batch_size=batch_size_),
                                          steps=None, start_delay_secs=1000, throttle_secs=1000)
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