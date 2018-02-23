#!/usr/bin/env python
#coding=utf-8

"""
This script is used for recognising continous sequantial patterns
"""

from __future__ import division
from __future__ import print_function

import tensorflow as tf
from tensorflow.python.ops import variable_scope
from tensorflow.python.framework import ops
from tensorflow.python.ops import array_ops
from six.moves import xrange
from six.moves import zip
import numpy as np
import cPickle
import random
import time
import sys
import os
import shutil
import cal_postproc_ccc_func

# define parameters
tf.app.flags.DEFINE_float("seed", 1, "random seed for experiments repeatable.")
tf.app.flags.DEFINE_float("max_seq_len", 7377, "random seed for experiments repeatable.") # 2000 for sewa; 7302 for recola
tf.app.flags.DEFINE_float("learning_rate", 0.9, "Learning rate.")
tf.app.flags.DEFINE_float("learning_rate_decay_factor", 0.99, "Learning rate decays by this much.")
tf.app.flags.DEFINE_float("max_gradient_norm", 5, "Clip gradients to this norm.")
tf.app.flags.DEFINE_integer("batch_size", 4, "Batch size to use during training.") # NOTE: when predicting, the value should be fully devided
tf.app.flags.DEFINE_integer("feat_dim", 952, "Feature dimensions.")  # 260 for audio; 334 for video
tf.app.flags.DEFINE_integer("g_n_classes_emo", 1, "Number of classes.")
tf.app.flags.DEFINE_integer("g_n_classes_ae", 1, "Number of classes.") # 130
tf.app.flags.DEFINE_integer("hidden_size", 100, "Size of each model layer.")
tf.app.flags.DEFINE_integer("num_layers", 5, "Number of layers in the model.")
tf.app.flags.DEFINE_boolean("use_lstm", False, "Use LSTM instead of GRU.")
tf.app.flags.DEFINE_integer("max_train_data_size", 0, "Limit on the size of training data (0: no limit).")
tf.app.flags.DEFINE_integer("steps_per_checkpoint", 10, "How many training steps to do per checkpoint.")
tf.app.flags.DEFINE_string("train_dir", "/home/zzhang12/work/aria/recola_audio_edu/train/", "Training directory.")
tf.app.flags.DEFINE_string("data_dir", "/home/zzhang12/work/aria/recola_audio_edu/data/recola_delay", "Training directory.")
tf.app.flags.DEFINE_string("base_dir", "/home/zzhang12/work/aria/recola_audio_edu", "Training directory.")
tf.app.flags.DEFINE_boolean("is_train", True, "Run a self-test if this is set to True.")
tf.app.flags.DEFINE_boolean("forward_only", False, "Set to True for interactive decoding.")

tf.app.flags.DEFINE_integer("run_num", 20, "number of overall runs")
tf.app.flags.DEFINE_integer("iter_num_substep", 10, "number of substep")
tf.app.flags.DEFINE_string("task", "val", "arousal or valence")
tf.app.flags.DEFINE_string("modal", "audiovideo", "arousal or valence")
tf.app.flags.DEFINE_string("delay", "60", "arousal or valence")
tf.app.flags.DEFINE_string("std", "std", "std or stdspk")
tf.app.flags.DEFINE_integer("addn", 1, "repetition times of additive element")
tf.app.flags.DEFINE_float("lamda", 0.01, "coefficience with reconstruction error")

FLAGS = tf.app.flags.FLAGS

g_n_dims = FLAGS.feat_dim + FLAGS.addn

random.seed(FLAGS.seed)
tf.set_random_seed(FLAGS.seed)

# model path
TRAIN_FOLD = FLAGS.modal + "_" + FLAGS.task + "_delay" + FLAGS.delay + "_" + FLAGS.std + "_addn" + str(FLAGS.addn) + "_l" + str(FLAGS.num_layers) + "_h" + str(FLAGS.hidden_size) + "_r" + str(FLAGS.run_num) + "_ss" + str(FLAGS.iter_num_substep) + "_lm" + str(FLAGS.lamda)
TRAIN_DIR = FLAGS.train_dir + "/" + TRAIN_FOLD


# placeholder for inputs, targets, and weights
g_network_inputs = tf.placeholder(tf.float32, shape=[None, FLAGS.batch_size, g_n_dims])


### netowrk structure
def model(network_inputs, hidden_units=100, num_layers=5, seq_len=7377, number_of_outputs=1):
    with tf.variable_scope("model"):
        stacked_gru = tf.contrib.rnn.MultiRNNCell([tf.contrib.rnn.GRUCell(hidden_units) for _ in range(num_layers)],
                                                  state_is_tuple=True)  ## to change num_layers

        # for emotion prediction
        g_weights_emo = {
            'w': tf.Variable(tf.truncated_normal([hidden_units, number_of_outputs], stddev=0.1, name="g_w_emo")),
            'b': tf.Variable(tf.truncated_normal([number_of_outputs], stddev=0.1, name="g_b_emo"))
        }
        # for perception uncertainty prediction
        g_weights_ae = {
            'w': tf.Variable(tf.truncated_normal([hidden_units, number_of_outputs], stddev=0.1, name="g_w_ae")),
            'b': tf.Variable(tf.truncated_normal([number_of_outputs], stddev=0.1, name="g_b_ae"))
        }
        network_inputs = tf.transpose(network_inputs, [1, 0, 2])
        outputs_a, state_out_a = tf.nn.dynamic_rnn(stacked_gru, network_inputs, dtype=tf.float32)

        outputs_a = tf.reshape(outputs_a, [-1, hidden_units])
        prediction_emo = tf.matmul(outputs_a, g_weights_emo['w']) + g_weights_emo['b']
        prediction_ae = tf.matmul(outputs_a, g_weights_ae['w']) + g_weights_ae['b']
        # prediction_emo = tf.reshape(prediction_emo, [-1, seq_len, number_of_outputs])
        # prediction_ae = tf.reshape(prediction_ae, [-1, seq_len, number_of_outputs])
        # prediction_emo = tf.unstack(tf.transpose(prediction_emo, [1, 0, 2]))
        # prediction_ae = tf.unstack(tf.transpose(prediction_ae, [1, 0, 2]))

    return prediction_emo, prediction_ae



#  outputs
g_outputs_emo, g_outputs_ae = model(g_network_inputs)

# save model
saver = tf.train.Saver(tf.global_variables(), max_to_keep=1)



################## testing
with tf.Session() as sess:
    print("Training ... ")
    ckpt = tf.train.get_checkpoint_state(TRAIN_DIR)
    if ckpt and ckpt.model_checkpoint_path:
        print("Reading model parameters from %s" % ckpt.model_checkpoint_path)
        saver.restore(sess, ckpt.model_checkpoint_path)
    else:
        print("Created model with fresh parameters.")
        sess.run(tf.global_variables_initializer())

    # predict the perception uncertainty
    feat = np.random.rand(1,FLAGS.feat_dim)
    g_network_inputs_batch = np.array([np.tile([np.append(feat,0)], (FLAGS.batch_size,1))])
    network_outputs_ae_batch = sess.run(g_outputs_ae, feed_dict={g_network_inputs: np.array(g_network_inputs_batch)})
    print(network_outputs_ae_batch)

    # update the input
    g_network_inputs_batch_updated = np.array([np.tile([np.append(feat, network_outputs_ae_batch[0][0])], (FLAGS.batch_size, 1))])
    # predict the emotion
    network_outputs_emo_batch = sess.run(g_outputs_emo, feed_dict={g_network_inputs: np.array(g_network_inputs_batch_updated)})
    print(network_outputs_ae_batch)

