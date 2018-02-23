'''
emax_parser.py
author: Johannes Wagner <wagner@hcm-lab.de>
created: 2017/09/17
Copyright (C) University of Augsburg, Lab for Human Centered Multimedia
'''

import math


def getOptions(opts, vars):

    opts['n_missing'] = 0            

    vars['valence'] = 0
    vars['arousal'] = 0
    vars['horizontal'] = 0
    vars['horizontal_avg'] = 0
    vars['horizontal_var'] = 0
    vars['vertical'] = 0    
    vars['vertical_avg'] = 0
    vars['vertical_var'] = 0
    vars['activity'] = 0
    vars['n_missing_count'] = 0    


def getSampleDimensionOut(dim, opts, vars):

    return 6


def transform(info, sin, sout, sxtra, board, opts, vars): 
    
    alpha = 0.75

    for n in range(0,sin.num):

        has_face = min(sin[n,0], 1)

        valence = vars['valence']
        arousal = vars['arousal']
        horizontal = vars['horizontal']
        horizontal_avg = vars['horizontal_avg']
        horizontal_var = vars['horizontal_var']
        vertical = vars['vertical']
        vertical_avg = vars['vertical_avg']
        vertical_var = vars['vertical_var']
        activity = vars['activity']

        n_missing = opts['n_missing']
        n_missing_count = vars['n_missing_count']

        if has_face > 0:
        
            n_missing_count = n_missing

            neutral = sin[n,6]
            anger = sin[n,7]
            disgust = sin[n,8]
            fear = sin[n,9]
            happy = sin[n,10]
            sadness = sin[n,11]
            surprise = sin[n,12]

            arousal = neutral + anger + disgust + happy + sadness + surprise - max(neutral,anger,disgust,happy,sadness,surprise)            
            valence = happy

            horizontal = min(1, max(0, (0.3 - abs(sin[n,147])) / 0.3))
            vertical = min(1, max(0, (0.3 - abs(sin[n,146])) / 0.3))

            horizontal_avg = alpha * horizontal_avg + (1-alpha) * horizontal
            horizontal_avg_diff = horizontal - horizontal_avg
            vertical_avg = alpha * vertical_avg + (1-alpha) * vertical
            vertical_avg_diff = vertical - vertical_avg
            horizontal_var = alpha * horizontal_var + (1-alpha) * math.pow(horizontal_avg_diff,2)
            vertical_var = alpha * vertical_var + (1-alpha) * math.pow(vertical_avg_diff,2)

            activity = min(1, 150 * (horizontal_var + vertical_var))

        else:

            if n_missing_count == 0:
                valence = 0
                arousal = 0
                horizontal = 0
                horizontal_avg = 0
                horizontal_var = 0
                vertical = 0
                vertical_avg = 0
                vertical_var = 0
                activity = 0
            else:
                n_missing_count = n_missing_count - 1            

        sout[n,0] = has_face
        sout[n,1] = valence
        sout[n,2] = arousal
        sout[n,3] = horizontal
        sout[n,4] = vertical
        sout[n,5] = activity

    vars['valence'] = valence
    vars['arousal'] = arousal
    vars['horizontal'] = horizontal
    vars['horizontal_avg'] = horizontal_avg
    vars['horizontal_var'] = horizontal_var
    vars['vertical'] = vertical
    vars['vertical_avg'] = vertical_avg
    vars['vertical_var'] = vertical_var
    vars['activity'] = activity
    vars['n_missing_count'] = n_missing_count
        