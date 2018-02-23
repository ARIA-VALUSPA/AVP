'''
avemotionf2e.py
authors: Johannes Wagner <wagner@hcm-lab.de>, Eduardo Coutinho <e.coutinho@imperial.ac.uk>
created: 2017/11/30
Copyright (C) University of Augsburg, Lab for Human Centered Multimedia
'''


# Import tensorflow. See: https://github.com/ninia/jep/issues/81
import sys
sys.argv = ['']
#import tensorflow.python as tf
#from tensorflow.python.platform import tf_logging as logging
import tensorflow.contrib as tfc
import tensorflow as tf

import collections
import numpy as np

def get_parameters(opts,vars):

    # vars['seq_length'] = 25*opts['rec_length']
    # # Buffers to store audio and video raw signals
    # vars['video_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    # vars['audio_buf'] = collections.deque(maxlen=vars['seq_length']) #*40
    # # Model parameters
    # vars['batch_size'] = 1 # Batch size to use (always one as it will work in real-time)
    # vars['model'] = 'both' # Model to be used: audio,video, or audiovideo
    # vars['checkpoint_dir'] = './models/model.ckpt-4129' # Location of model
    # vars['hidden_units'] = 256 # Model parameter: number of hidden units in each LSTM layer
    # vars['hidden_layers'] = 2 # Model parameter: number of LSTM layers
    # #vars['num_examples'] = 50
    #
    return 0 # FLAGS

def load_model(vars):

     # vars['tf_session'] = sess
     # vars['tf_state'] = state_out
     # vars['tf_coord'] = coord
     # vars['tf_prediction'] = prediction
     # vars['tf_st_o'] = st_o
     # vars['tf_frames'] = frames
     # vars['tf_audio'] = audio
     # vars['tf_initial_state_t'] = initial_state_t

    return 0 # FLAGS

def getOptions(opts,vars):

    pass


def consume_enter(sin, board, opts, vars):

    get_parameters(opts,vars)
    load_model(vars)

    pass


def consume(info, sin, board, opts, vars):

    video_feature = np.asmatrix(sin[0])
    audio_feature = np.asmatrix(sin[1])

    print(video_feature)
    print(audio_feature)

    # vars['video_buf'].append(np.asarray(sin[0]) / 255)
    # vars['audio_buf'].append(np.asmatrix(sin[1]))

    # if len(vars['video_buf']) >= vars['seq_length']:
    #
    #     tf_video = np.array(vars['video_buf']).reshape(1,vars['seq_length'],96, 96, 3)
    #     tf_audio = np.array(vars['audio_buf']).reshape(1,vars['seq_length'],640)
    #
    #     predictions = compute_av(vars,tf_video,tf_audio)
    #     print('\t* Arousal: ' + str(predictions[0]))
    #     print('\t* Valence: ' + str(predictions[1]))
    #
    #     # what to do if frames are received in the meantime?
    #     vars['video_buf'].clear()
    #     vars['audio_buf'].clear()

# def compute_av(vars,video_in,audio_in):
#
#     #
#     try:
#         pred, tf_state = vars['tf_session'].run(
#                                      [vars['tf_prediction'],vars['tf_st_o']], # [prediction],
#                                      feed_dict={vars['tf_frames']: video_in,
#                                                 vars['tf_audio']: audio_in,
#                                                 vars['tf_initial_state_t']: vars['tf_state']}
#                                )
#
#         predictions = np.mean(np.reshape(pred, (-1, 2)),axis=0)
#         vars['tf_state'] = tf_state
#
#     except Exception as e:
#         print('Exception : ', e)
#         vars['tf_coord'].request_stop(e)
#         predictions = [0.0,0.0]
#
#     return predictions
#
def consume_flush(sin, board, opts, vars):

    pass
