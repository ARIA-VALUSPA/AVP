'''
ssi_event.py
author: Johannes Wagner <wagner@hcm-lab.de>
created: 2016/03/08
Copyright (C) University of Augsburg, Lab for Human Centered Multimedia

Sends mean of the second stream within series of non-zero values in the first stream.
'''


from json import dumps
import xmltodict


def getOptions(opts, vars):

    opts['address'] = 'event@json'
    opts['debug'] = False


def getEventAddress(opts, vars):

    return opts['address']


def listen_enter(opts, vars):

    pass


def update(event, board, opts, vars):    
    
    try:

        xml = str(event.data)
        data = xmltodict.parse(xml)
        json = dumps(data, indent=2, sort_keys=False)
     
        json = json.replace('@', '')        
        json = json.replace('\n', '\r\n')        

        if opts['debug']:
            print(json)

        board.update(event.time, event.dur, opts['address'], json)

    except Exception as e:
        
        print('could not convert xml to json: ', e)



def listen_flush(opts, vars):

    pass