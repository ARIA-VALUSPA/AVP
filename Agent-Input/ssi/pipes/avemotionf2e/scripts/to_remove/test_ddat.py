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
import tensorflow.contrib as tfc
slim = tfc.slim
from six.moves import xrange
from six.moves import zip
import numpy as np
#import cPickle
import random
import time
import sys
import os
import shutil
#import cal_postproc_ccc_func

# define parameters
vars = {}
vars["batch_size"] =  1 #"Batch size to use during training.") # NOTE: when predicting, the value should be fully devided
vars["feat_dim"] = 952 #"Feature dimensions.")  # 260 for audio; 334 for video
vars["checkpoint_arousal_dir"] = "..\\models\\arousal" #..\\models\\audiovideo_aro_delay60_std_addn1_l5_h100_r20_ss10_lm0.01_best\\" #"Arousal trained model directory.")
vars["checkpoint_valence_dir"] = "..\\models\\valence" #"..\\models\\audiovideo_val_delay60_std_addn1_l5_h100_r20_ss10_lm0.01_best\\" #"Arousal trained model directory.")
vars["arousal_model"] = "..\\models\\arousal\\lstm.ckpt-230" #"Arousal trained model directory.")
vars["valence_model"] = "..\\models\\valence\\lstm.ckpt-330" #"Valence trained model directory.")
vars["addn"] = 1 # "repetition times of additive element")

### netowrk structure
def load_model(vars,task,hidden_units=100, num_layers=5, seq_len=7377, number_of_outputs=1):

    # all_vars = tf.global_variables() #tf.all_variables()
    # if task == 'valence':
    #     model_vars = [k for k in all_vars if k.name.startswith('model_'+task)]
    # else:
    #     model_vars = all_vars
    # print(model_vars)
    with tf.variable_scope('model_'+task):

        network_inputs = tf.placeholder(tf.float32, shape=[None, vars['batch_size'], vars['feat_dim'] + vars['addn']])

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

        # Compute Arousal
        print('Loading ' + task + ' model from:')
        print("> " + vars['checkpoint_'+task+'_dir'])
        print("  Reading model parameters from %s" % vars[task+'_model'])

        vars['tf_session_'+task] = tf.Session()
        saver = tf.train.Saver(tf.global_variables(scope='model_'+task), max_to_keep=1)
        saver.restore(vars['tf_session_'+task], vars[task+'_model'])
        #tf.train.start_queue_runners(sess=sess)
        #saver.restore(sess, FLAGS.arousal_model)

        vars['tf_stateout_'+task] = state_out_a
        vars['tf_prediction_'+task] = prediction_emo
        vars['tf_prediction_'+task+'u'] = prediction_ae
        vars['tf_frames_'+task] = network_inputs
        vars['tf_saver_'+task] = network_inputs

    return vars

#  Load model
vars = load_model(vars,'arousal')
vars = load_model(vars,'valence')

# Compute Arousal
for task in ['arousal','valence']:
    with vars['tf_session_'+task] as sess:

        # predict the perception uncertainty
        feat = np.random.rand(1,vars['feat_dim'])
        g_network_inputs_batch = np.array([np.tile([np.append(feat,0)], (vars['batch_size'],1))])
        network_outputs_ae_batch = sess.run(vars['tf_prediction_'+task+'u'], feed_dict={vars['tf_frames_'+task]: np.array(g_network_inputs_batch)})
        print(network_outputs_ae_batch)

        # update the input
        g_network_inputs_batch_updated = np.array([np.tile([np.append(feat, network_outputs_ae_batch[0][0])], (vars['batch_size'], 1))])
        # predict the emotion
        network_outputs_emo_batch = sess.run(vars['tf_prediction_'+task], feed_dict={vars['tf_frames_'+task]: np.array(g_network_inputs_batch_updated)})
        print(network_outputs_ae_batch)
