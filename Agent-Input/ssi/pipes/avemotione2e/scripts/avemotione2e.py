'''
avemotione2e.py
authors: Johannes Wagner <wagner@hcm-lab.de>, Eduardo Coutinho <e.coutinho@imperial.ac.uk>
created: 2017/07/25
Copyright (C) University of Augsburg, Lab for Human Centered Multimedia
'''

import numpy as np
import math
from pathlib import Path
import time
import os

# Import tensorflow. See: https://github.com/ninia/jep/issues/81
import sys
sys.argv = ['']
#import tensorflow.python as tf
#from tensorflow.python.platform import tf_logging as logging
import tensorflow.contrib as tfc
import tensorflow as tf

import collections

import models

slim = tfc.slim

# def tf_install_and_import():
#     import importlib
#     try:
#         print('here1')
#         importlib.import_module('tensorflow')
#     except ImportError:
#         print('here2')
#         import pip
#         pip.main(['install', '--upgrade', 'https://storage.googleapis.com/tensorflow/windows/gpu/tensorflow_gpu-0.12.1-cp35-cp35m-win_amd64.whl'])
#
#     finally:
#         print('here3')
#         globals()['tensorflow'] = importlib.import_module('tensorflow')


def get_parameters(opts,vars):

    vars['seq_length'] = 25*opts['rec_length']
    # Buffers to store audio and video raw signals
    vars['video_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    vars['audio_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    # Model parameters
    vars['batch_size'] = 1 # Batch size to use (always one as it will work in real-time)
    vars['model'] = 'both' # Model to be used: audio,video, or audiovideo
    vars['checkpoint_dir'] = './models/model.ckpt-4129' # Location of model
    vars['hidden_units'] = 256 # Model parameter: number of hidden units in each LSTM layer
    vars['hidden_layers'] = 2 # Model parameter: number of LSTM layers
    #vars['num_examples'] = 50

    return 1 # FLAGS

def load_model(vars):

      frames = tf.placeholder(tf.float32, shape=(vars['batch_size'],vars['seq_length'],96, 96, 3))
      audio  = tf.placeholder(tf.float32, shape=(vars['batch_size'],vars['seq_length'],640))

      # LSTM initial sates placeholder
      initial_state_ph = tf.placeholder(tf.float32, [2, 2, vars['batch_size'], vars['hidden_units']])
      l = tf.unstack(initial_state_ph, axis=0)
      initial_state_t = tuple(
                        [ tf.nn.rnn_cell.LSTMStateTuple(l[idx][0],l[idx][1])
                            for idx in range(vars['hidden_layers'])]
                            )
      init_array = np.zeros((1, vars['hidden_units']))
      state_out = tuple([tf.nn.rnn_cell.LSTMStateTuple(init_array, init_array)
                            for idx in range(vars['hidden_layers'])])

      # Create model
      with slim.arg_scope(slim.nets.resnet_utils.resnet_arg_scope()):
            prediction, st_o = models.get_model(vars['model'])(
                                frames, audio,
                                prev_hidden_states=initial_state_t,
                                hidden_units=vars['hidden_units'])

      coord = tf.train.Coordinator()
      variables_to_restore = slim.get_variables_to_restore()

      #num_batches = int(math.ceil(vars['num_examples'] / (float(vars['batch_size'] * vars['seq_length']) )))

      saver = tf.train.Saver(variables_to_restore)

      sess = tf.Session()
      saver.restore(sess, vars['checkpoint_dir'])
      tf.train.start_queue_runners(sess=sess)

      vars['tf_session'] = sess
      vars['tf_state'] = state_out
      vars['tf_coord'] = coord
      vars['tf_prediction'] = prediction
      vars['tf_st_o'] = st_o
      vars['tf_frames'] = frames
      vars['tf_audio'] = audio
      vars['tf_initial_state_t'] = initial_state_t

      return 0 #sess, coord, state_out, prediction, st_o, frames, audio, initial_state_t

def getOptions(opts,vars):

    opts['rec_length'] = 5 # in case it was not set

    pass


def consume_enter(sin, board, opts, vars):

    get_parameters(opts,vars)
    load_model(vars)

    pass


def compute_av(vars,video_in,audio_in):

    #
    try:
        pred, tf_state = vars['tf_session'].run(
                                     [vars['tf_prediction'],vars['tf_st_o']], # [prediction],
                                     feed_dict={vars['tf_frames']: video_in,
                                                vars['tf_audio']: audio_in,
                                                vars['tf_initial_state_t']: vars['tf_state']}
                               )

        predictions = np.mean(np.reshape(pred, (-1, 2)),axis=0)
        vars['tf_state'] = tf_state

    except Exception as e:
        print('Exception : ', e)
        vars['tf_coord'].request_stop(e)
        predictions = [0.0,0.0]

    return predictions


def consume(info, sin, board, opts, vars):

    vars['video_buf'].append(np.asarray(sin[0]) / 255)
    vars['audio_buf'].append(np.asmatrix(sin[1]))

    if len(vars['video_buf']) >= vars['seq_length']:

        # check lenght!!!!

        # print('length of video buffer: ',len(vars['video_buf']))
        # print('\t. shape of video frame: ',vars['video_buf'][0].shape)
        # print('length of audio buffer: ',len(vars['audio_buf']))
        # print('\t. shape of audio frame: ',vars['audio_buf'][0].shape)
        tf_video = np.array(vars['video_buf']).reshape(1,vars['seq_length'],96, 96, 3)
        tf_audio = np.array(vars['audio_buf']).reshape(1,vars['seq_length'],640)
        # print(tf_audio.shape)
        # print(tf_video.shape)

        predictions = compute_av(vars,tf_video,tf_audio)
        print('\t* Arousal: ' + str(predictions[0]))
        print('\t* Valence: ' + str(predictions[1]))

        # what to do if frames are received in the meantime?
        vars['video_buf'].clear()
        vars['audio_buf'].clear()


def consume_flush(sin, board, opts, vars):

    # runs at the end

    pass
