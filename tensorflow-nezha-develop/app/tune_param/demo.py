#!usr/bin/env python
# -*- coding:utf-8 -*-

'''
@author:houyawei
@file:main.py
@time:2018/10/23
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
from model import train


parameters = {
    'learning_rate_': [0.00001, 0.00005, 0.0001, 0.0005, 0.001],
    'batch_size_' : [50, 100, 150, 200, 250, 300],
    'l2_reg_' : [0.000001, 0.00001, 0.0001],
    'deep_layers_' : ['512,512,256', '1024,512,512', '1024,512,256', '512,256,128'],
    'cross_layers_': [3, 4, 5],
    'dropout_' : ['0.99,0.99,0.99', '0.95,0.95,0.95']
}


def gridsearch():
    i = 1
    field_size_ = 33
    embedding_size_ = 6
    feature_size_ = field_size_ * (embedding_size_ + 1) + 1
    for learning_rate_ in parameters['learning_rate_']:
        for batch_size_ in parameters['batch_size_']:
            for l2_reg_ in parameters['l2_reg_']:
                for deep_layers_ in parameters['deep_layers_']:
                    for cross_layers_ in parameters['cross_layers_']:
                        for dropout_ in parameters['dropout_']:
                            print "iteration:", i
                            print learning_rate_, batch_size_, l2_reg_, deep_layers_, cross_layers_, dropout_

                            # ------bulid Tasks------
                            model_params2 = {
                                "field_size": field_size_,
                                "embedding_size": embedding_size_,
                                "learning_rate": learning_rate_,
                                "batch_norm_decay": 0.9,
                                "batch_size": batch_size_,
                                "l2_reg": l2_reg_,
                                "batch_norm": False,
                                "optimizer": "adam",
                                "deep_layers": deep_layers_,
                                "cross_layers": cross_layers_,
                                "dropout": dropout_
                            }

                            print 'i=', i, ',model_params2=', model_params2
                            try:
                                main1(model_params2)
                            except Exception as e:
                                print e
                            i = i + 1


tf.app.run(gridsearch())


