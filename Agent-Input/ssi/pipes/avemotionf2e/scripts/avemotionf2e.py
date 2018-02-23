'''
avemotione2e.py
authors: Johannes Wagner <wagner@hcm-lab.de>, Eduardo Coutinho <e.coutinho@imperial.ac.uk>
created: 2017/07/25
Copyright (C) University of Augsburg, Lab for Human Centered Multimedia
'''

import numpy as np
import math
# from pathlib import Path
import time
import os

# Import tensorflow. See: https://github.com/ninia/jep/issues/81
import sys
sys.argv = ['']
#import tensorflow.python as tf
#from tensorflow.python.platform import tf_logging as logging
import tensorflow as tf
import collections
#import models

def get_parameters(opts,vars):

    vars['seq_length'] = 25*opts['rec_length']
    # Buffers to store audio and video raw signals
    vars['video_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    vars['audio_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    # Model parameters
    vars["batch_size"] =  1 #"Batch size to use during training.") # NOTE: when predicting, the value should be fully devided
    vars["feat_dim"] = 952 #"Feature dimensions.")  # 260 for audio; 692 for video
    vars["arousal_model"] = "models\\arousal\\lstm.ckpt-230" #"Arousal trained model directory.")
    vars["valence_model"] = "models\\valence\\lstm.ckpt-330" #"Valence trained model directory.")
    vars["addn"] = 1 # "repetition times of additive element")

    return 1

def load_model(vars,task,hidden_units=100, num_layers=5, seq_len=7377, number_of_outputs=1):

    with tf.variable_scope("model_"+task):

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
        print("Loading " + task + " model from:")
        print("> " + vars[task + '_model'])
        print("  Reading model parameters from %s" % vars[task + '_model'])

        vars['tf_session_'+ task] = tf.Session()
        saver = tf.train.Saver(tf.global_variables(scope='model_'+task), max_to_keep=1)
        saver.restore(vars['tf_session_'+ task], vars[task + '_model'])

        vars['tf_stateout_'+task] = state_out_a
        vars['tf_prediction_'+task] = prediction_emo
        vars['tf_prediction_'+task+'u'] = prediction_ae
        vars['tf_frames_'+task] = network_inputs
        vars['tf_saver_'+task] = saver

    return 0


def getOptions(opts,vars):

    opts['rec_length'] = 5 # in case it was not set

    pass


def consume_enter(sin, board, opts, vars):

    get_parameters(opts,vars)
    load_model(vars,'arousal')
    load_model(vars,'valence')

    pass

def compute_output(vars,features,task): #,video_in):

    # Compute Arousal
    try:

        g_network_inputs_batch = np.array([np.tile([np.append(features,0)], (10,vars['batch_size']))]) #(vars['batch_size'],1)
        print('g_network_inputs_batch.shape='+str(g_network_inputs_batch.shape))
        # predict the perception uncertainty
        network_outputs_ae_batch = vars['tf_session_'+task].run(
                                        vars['tf_prediction_'+task+'u'],
                                        feed_dict={vars['tf_frames_'+task]: np.array(g_network_inputs_batch)})
        # update the input
        g_network_inputs_batch_updated = np.array([np.tile([np.append(features, network_outputs_ae_batch[0][0])], (10, vars['batch_size']))])
        # predict the emotion
        network_outputs_emo_batch = vars['tf_session_'+task].run(
                                        vars['tf_prediction_'+task],
                                        feed_dict={vars['tf_frames_'+task]: np.array(g_network_inputs_batch_updated)})

        #predictions = np.mean(np.reshape(pred, (-1, 2)),axis=0)
        #vars['tf_state'] = tf_state

    except Exception as e:
        print('Exception : ', e)
        network_outputs_emo_batch = [999.0]
        network_outputs_ae_batch = [999.0]

    return network_outputs_emo_batch, network_outputs_ae_batch


def consume(info, sin, board, opts, vars):
#def consume(opts, vars):

    # vars['video_buf'].append(np.asarray(sin[0]) / 255)
    # vars['audio_buf'].append(np.asmatrix(sin[1]))
    video_features = sin[0]
    audio_features = sin[1]
    print('len(audio_features)=',str(len(audio_features))) # 692
    print('len(video_features)=',str(len(video_features))) # 261
    vars['video_buf'] = np.random.rand(1, vars['seq_length'] * 261)
    vars['audio_buf'] = np.random.rand(1, vars['seq_length'] * 261)

    # Dummy data
    av_features = np.random.rand(1,vars['feat_dim'])
    print('av_features.shape='+str(av_features.shape))

    # Compute Arousal
    predictions = compute_output(vars,av_features,'arousal')
    print('\t* Arousal: ' + str(np.mean(predictions[0])))
    print('\t* Arousal (uncertainty): ' + str(np.mean(predictions[1])))
    # Compute Valence
    predictions = compute_output(vars,av_features,'valence')
    print('\t* Valence: ' + str(np.mean(predictions[0])))
    print('\t* Valence (uncertainty): ' + str(np.mean(predictions[1])))


#opts, vars = {}, {}
#getOptions(opts, vars)
#get_parameters(opts, vars)
#consume(opts, vars)

def consume_flush(sin, board, opts, vars):

    # runs at the end

    pass
