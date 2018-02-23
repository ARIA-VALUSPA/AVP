from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import tensorflow as tf

def model(network_inputs, prev_hidden_states, hidden_units=100, num_layers=5, seq_len=7377, number_of_outputs=1):
    with tf.variable_scope("model"):
        ### network graphic
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

        ### feed data into network
        network_inputs = tf.transpose(network_inputs, [1, 0, 2])
        outputs_a, state_out_a = tf.nn.dynamic_rnn(stacked_gru, network_inputs, dtype=tf.float32)

        outputs_a = tf.reshape(outputs_a, [-1, hidden_units])
        prediction_emo = tf.matmul(outputs_a, g_weights_emo['w']) + g_weights_emo['b']
        prediction_ae = tf.matmul(outputs_a, g_weights_ae['w']) + g_weights_ae['b']
        prediction_emo = tf.reshape(prediction_emo, [-1, seq_len, number_of_outputs])
        prediction_ae = tf.reshape(prediction_ae, [-1, seq_len, number_of_outputs])
        prediction_emo = tf.unstack(tf.transpose(prediction_emo, [1, 0, 2]))
        prediction_ae = tf.unstack(tf.transpose(prediction_ae, [1, 0, 2]))

        # tf.concat((network_inputs, prediction_ae), 2, name='concat')
    return prediction_emo, state_out


def get_model(name):
    """Complete me...
    Args:
    Returns:
    """

    def wrapper(*args, **kwargs):
        return model(**kwargs)
    return wrapper


    # name_to_fun = {'audio': audio_model, 'video': video_model, 'both': combined_model}
    #
    # if name in name_to_fun:
    #     model = name_to_fun[name]
    # else:
    #     raise ValueError('Requested name [{}] not a valid model'.format(name))
    #
    # def wrapper(*args, **kwargs):
    #     return recurrent_model(model(*args), **kwargs)
    #
    # return wrapper
