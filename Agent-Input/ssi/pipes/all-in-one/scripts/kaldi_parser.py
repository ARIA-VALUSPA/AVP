
import re
import json

def getEventAddress(opts, vars):
    return opts['address']


def getOptions(opts,vars):

    opts['address'] = 'words@kaldi'
    opts['nbest'] = 3
    opts['mode'] = 'utt'
    opts['language'] = 'en'

    vars['line'] = ''
    vars['json_line'] = ''
    vars['header'] = ''
    vars['time'] = 0


def send_enter(opts, vars):
    pass


def listen_enter(opts, vars):
    pass


def parse(msg, line, header_in, opts):

    # these varibles should be wset somehwere - not need here for noe, but needed for DM
    # asr_mode = 'utt'
    # asr_nbest = 3

    # Is there a new utterance?
    new_utt = "RESULT:NUM" in msg
    # If yes, then process header to get output info
    if new_utt == True:
        #header = msg.split(',')
        header = re.split('[, \n]',msg)
        if not ("RESULT:NUM" in header[0] and len(header) >= 4 and len(header[3].split("=")) > 1):
            header = header_in
        #if "RESULT:NUM" in header[0]:
        # If idur = 0 then ignore header (same transcription)
        #if header[3].split("=")[1] == "0":
        #    header = header_in
    else:
        header = header_in

    line = line + msg

    json_line = ''

    # Test if partial or complete results was received
    end_reco = "RESULT:DONE" in line
    part_reco = "RESULT:PART" in line
    done = any([end_reco,part_reco])

    # If it was , then process and output the transcription
    if done:
        tokens = line.split('\n')

        # Process transcription in string format
        line = ''
        for token in tokens:
            if not any((c in ':,-=.') for c in token):
                line = line + token + ' '
        line = line.strip()

        # Process  transcription in json format
        json_data = {}
        json_data['content'] = "ASR_output"
        json_data['language'] = opts['language']
        json_data['nbest'] = opts['nbest']
        json_data['mode'] = opts['mode']
        json_data['partial'] = part_reco
        #json_data['istart'] = header[4].split("=")[1]
        #json_data['iend'] = header[5].split("=")[1].strip()
        json_data['rdur'] = header[2].split("=")[1]
        json_data['idur'] = header[3].split("=")[1]

        tokens_nbest = re.split('[(*)]',line)

        transcriptions = []

        if (part_reco == True and opts['mode'] == 'inc') or opts['nbest'] == 1:
            if not tokens_nbest[0].strip() == "":
                transcriptions.append({
                                       'id': '0',
                                       'nwords': len(tokens_nbest[0].strip().split(" ")),
                                       'text': tokens_nbest[0].strip(),
                                       })
        else:
            for token_nbest_idx in range(0,len(tokens_nbest)-1,2):
                #if not tokens_nbest[token_nbest_idx].strip() == "":
                transcriptions.append({
                               'id': tokens_nbest[token_nbest_idx+1].strip(),
                               'nwords': len(tokens_nbest[token_nbest_idx].strip().split(" ")),
                               'text': tokens_nbest[token_nbest_idx].strip(),
                               })
        json_data['transcriptions'] = transcriptions

        # Output transcriptions
        if len(transcriptions) > 0:
            json_line = json.dumps(json_data, indent=2, sort_keys=False)

    return line, json_line, done, header


def update(event, board, opts, vars):

    line = vars['line']
    json_line = vars['json_line']
    header = vars['header']
    if not line:
        vars['time'] = event.time

    line, json_line, done, header = parse(event.data, line, header, opts)

    if done:
        time = vars['time']
        dur = (event.time - vars['time']) + event.dur
        board.update(time, dur, opts['address'], json_line)
        line = ''
        json_line = ''

    vars['line'] = line
    vars['json_line'] = json_line
    vars['header'] = header


def listen_flush(opts, vars):
    pass


def send_flush(opts, vars):
    pass
