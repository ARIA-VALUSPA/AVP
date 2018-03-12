from __future__ import print_function

import wave, struct, socket, sys, time
import threading
import pyaudio
import re
import json

if (sys.version_info > (3, 0)):
    # Python 3 code in this block
    from queue import Queue, Empty
else:
    # Python 2 code in this block
    from Queue import Queue, Empty
try:
   import webrtcvad
   vad_available = True
   print('* VAD is turned ON')
except:
   vad_available = False
   print('* VAD is turned OFF')
   print("\twebrtcvad is missing, vad will not be available.")
   print("\tPlease install webrtcvad in order to have the VAD feature enabled!")
   print("\tThis can usually be done running the following command:")
   print("\t  pip install --user webrtcvad")

RATE  = 16000
CHUNK = 160
CHANNELS = 1
FORMAT = pyaudio.paInt16

ASR_MODE = ''
ASR_NBEST = 1
sent_audio_sec = 0
gsegments = []


# Not need for threading, pyaudio already deals with it
class recorder(object):
   def __init__(self, queue, event):
       p = pyaudio.PyAudio()
       self.size = CHUNK
       self.queue = queue
       self.event = event
       self.stream = p.open(format = FORMAT, channels = CHANNELS, rate = RATE,
                       input = True, frames_per_buffer = CHUNK, stream_callback=self.get_callback())
       self.stream.start_stream()

   def get_callback(self):
        def callback(in_data, frame_count, time_info, status):
            if not self.event.isSet():
               self.queue.put(in_data)
            else:
               return in_data, pyaudio.paComplete
            return in_data, pyaudio.paContinue
        return callback


class send_live_audio(threading.Thread):
   def __init__(self, socket, queue, event, vad_active=False):
       threading.Thread.__init__(self)
       self.socket = socket
       self.queue = queue
       self.event = event
       self.vad_active = vad_active
       if vad_available and vad_active:
          self.vad = webrtcvad.Vad(2)

   def send_audio(self, msg):
       global sent_audio_sec
       sent_audio_sec += float(len(msg)) / 2 / RATE
       packet = struct.pack("<i", len(msg)) + msg
       #print("sending %d" % len(packet))
       self.socket.sendall(packet)

   def run(self):
       #oldspeech = 0
       sample_count = 0
       cur_state = 0
       old_state = 0
       buffer = bytes()
       state_counts = {0: 0, 1: 0}
       maxbuf = 10 * CHUNK
       while not self.event.isSet():
        #    print "send" + str(len(self.queue.get())) + "\r"
               try:
                  msg = self.queue.get(timeout=1)
               except Empty:
                  #print "empty"
                  if cur_state:
                     self.send_audio(buffer)
                     self.send_audio(bytes())
                     buffer = bytes()
                     cur_state = 0
                  continue
               if not vad_available or not self.vad_active:
                  self.send_audio(msg)
                  continue
               sample_count += len(msg)
               # Force end of segment
               if len(msg) < CHUNK * 2:
                  #n = CHUNK - len(msg) / 2
                  #msg += struct.pack("<%dh" % n, *([0] * n))
                  #msg = ""
                  old_state = cur_state
                  cur_state = 0
                  isspeech = 0
               else:
                  isspeech = self.vad.is_speech(msg, RATE)
               state_counts[isspeech] += 1
               if isspeech or cur_state:
                  buffer += msg #msg.decode()
               else:
                  if len(buffer) > maxbuf:
                     buffer = buffer[len(buffer) - maxbuf:]
                  buffer += msg
               if isspeech != cur_state:
                  state_counts[cur_state] = 0
               else:
                  for s in state_counts:
                     if s != cur_state:
                        state_counts[s] = 0
               # state switching
               #print isspeech, state_counts, len(buffer)
               if state_counts[isspeech] >= 10:
                  old_state = cur_state
                  cur_state = isspeech
               if len(buffer) and cur_state:
                  if not old_state:
                     start_time = sample_count - len(buffer)
                  self.send_audio(buffer)
                  buffer = bytes()
               if old_state and not cur_state:
                  end_time = sample_count
                  gsegments.append((start_time, end_time))
                  self.send_audio(bytes())
                  buffer = bytes()

class reader(threading.Thread):
   def __init__(self, sock, event, asr_mode, asr_nbest, log):
       threading.Thread.__init__(self)
       self.sock = sock
       self.event = event
       self.asr_mode = asr_mode
       self.asr_nbest = asr_nbest
       self.log = log
       self.header = None
       self.prev_start_time = -1.0
       self.line = ""
       self.done = False
       self.idur = 0.0
   def run(self):
       while not self.event.isSet():
          #msg = str(self.sock.recv(4096))
          msg = self.sock.recv(256)# .decode('utf-8')
          if len(msg) == 0:
             break
          else:
             if self.log == 1:
                 print(msg)
             else:
               self.line = self.line + msg.decode('utf-8')
               while True:
                 # Is there a new utterance?
                 new_utt = "RESULT:NUM" in self.line
                 # If yes, then process header to get output info
                 if new_utt:
                     header = re.split('[, \n]', self.line)
                     cur_start_time = float(header[4].split("=")[1])
                     if "RESULT:NUM" in header[0] and len(header) >= 4 and len(header[3].split("=")) > 1:
                         # If idur = 0 then ignore header (same transcription)
                         # Otherwise, save new header (new transcription)
                         #if not cur_start_time == self.prev_start_time:
                         #if not header[3].split("=")[1] == "0":
                         self.header = header
                         self.prev_start_time = cur_start_time
                             #print("new-header:\t" + msg)
                             #print("new-header_split:\t" + str(header))
                 # Add to line

                 # Test if partial or complete results was received
                 end_reco = "RESULT:DONE" in self.line
                 part_reco = "RESULT:PART" in self.line
                 done = any([end_reco,part_reco])
                 if not done or not self.header:
                     break

                 if end_reco and self.header:
                     self.idur += float(self.header[3].split("=")[1])


                 # If it was , then process and output the transcription
                 if done and self.header:
                     tokens = self.line.split('\n')
                     self.line = ''

                     # Process transcription in string format
                     line = ''
                     for i, token in enumerate(tokens):
                         if not any((c in ':,-=.') for c in token):
                             line = line + token + ' '
                         if i>0 and "RESULT:DONE" in token:
                              #while i <= len(tokens) - 2 and tokens[i+1] == "": i+=1
                              self.line = '\n'.join(tokens[i+1:])
                              break
                        #  if i>0 and "RESULT:NUM" in token:
                        #      self.line = '\n'.join(tokens[i:])
                        #      break
                     line = line.strip()

                     # Process  transcription in json format
                     json_data = {}
                     json_data['content'] = "ASR_output"
                     json_data['nwords'] = self.header[0].split("=")[1]
                     if len(gsegments) > 0:
                         seg_time = gsegments.pop(0)
                     else:
                         seg_time = [0.,0.]
                     json_data['istart'] = float(seg_time[0]) / RATE / 2
                     json_data['iend'] = float(seg_time[1]) / RATE / 2
                     json_data['rdur'] = self.header[2].split("=")[1]
                     json_data['idur'] = self.header[3].split("=")[1]

                     tokens_nbest = re.split('[(*)]',line)

                     transcriptions = []

                     # In inc mode only one potential output is received
                     # even if nbest > 1. All nbest outputs are only sent
                     # when the utterance ends
                     if (part_reco == True and self.asr_mode == 'inc') or self.asr_nbest == 1:
                          #if not tokens_nbest[0].strip() == "":
                             transcriptions.append({
                                                    'id': '0',
                                                    'text': tokens_nbest[0].strip()
                                                    })
                     else:
                         for token_nbest_idx in range(0,len(tokens_nbest)-1,2):
                             #if not tokens_nbest[token_nbest_idx].strip() == "":
                                 transcriptions.append({
                                                'id': tokens_nbest[token_nbest_idx+1].strip(),
                                                'text': tokens_nbest[token_nbest_idx].strip()
                                                })
                     json_data['transcriptions'] = transcriptions

                     # Print transcriptions
                     if not len(transcriptions) == 0:
                         print('***********************************************')
                         print('\n-----\nText:\n-----\n' + line)
                         print('\n-----\nJSON:\n-----\n' + json.dumps(json_data, indent=2, sort_keys=False))

                     # Reset the line
                     self.header = None


def send_file_audio(queue, wav):
    print("Sending audio file in chunks ...")
    fwave = wave.open(wav)
    rate = fwave.getframerate()
    chan = fwave.getnchannels()
    if rate != RATE or chan != CHANNELS:
        print("Unsupported format (must be MONO and 16kHz)")
        exit(-1)
    size = fwave.getnframes()
    print("\t. #frames= " + str(size))
    for i in range(0, size, CHUNK):
        # print "\t. send chunk " + str(i)
        msg = fwave.readframes(CHUNK)
        if len(msg) == 2 * CHUNK:
            queue.put(msg)
        #sock.send(struct.pack("<i", len(msg)) + msg)
    # End of stream pack
    queue.put(bytes())
    #sock.send(struct.pack("<i", 0))
    return float(size) / rate

import argparse

# def onKeyPress(event):
#     text.insert('end', 'You pressed %s\n' % (event.char, ))

def main():
   parser = argparse.ArgumentParser(description="minimal client to send audio to the ASR server")
   parser.add_argument("-H", "--host", default="localhost", help="ASR server hostname")
   parser.add_argument("-p", "--port", type=int, default=8888, help="ASR server port")
   parser.add_argument("-am", "--asr_mode", default="utt", help="mode label ('utt' or 'inc') used to initialise ASR")
   parser.add_argument("-an", "--asr_nbest", default=1, help="n_best value used to initialise ASR")
   parser.add_argument("-m", "--mode", default="live", help="Stream live or recorded audio (live/rec).")
   #parser.add_argument("-v", "--vad", action="store_true", help="Apply vad on recorded audio")
   parser.add_argument("-i", "--input_wav", default="test_audio/test_matthew.wav", help="Audio file to transcribe (needed in rec mode)")
   parser.add_argument("-l", "--log_flag", default=0, help="Log ASR returned messaged in raw format instead of processed messages (for debugging)")
   args = parser.parse_args()

   # print("\n> ASR_MODE is " + args.asr_mode)
   # if args.asr_mode == 'inc':
   #     vad_flag = False
   #     print(' . VAD is turned OFF by default in incremental mode. ' +
   #           'The ASR server will deal with the segmentation ' +
   #           '(make sure that --endpoint is set to True on the ASR server).')
   # else:
   #     vad_flag = True
   #     print(' . VAD is turned ON by default in utterance mode. ' +
   #           'This client will deal with the segmentation ' +
   #           '(make sure that --endpoint is set to False on the ASR server).')
   # print("> ASR_NBEST = " + str(args.asr_nbest))
   #print("> log_flag = " + str(args.log_flag))
   vad_flag = True

   # Socket for connecting to the ASR machine
   sock = socket.create_connection((args.host, args.port))

   # Queue for live recording and sending manipulating the same audio data array
   queue = Queue()

   # Event to interrupt threads
   interrupt_event = threading.Event()

   # Stream live audio
   if args.mode == "live":
       print("> MODE: stream live audio")
       audio = recorder(queue,interrupt_event)

   # Stream recorded audio
   if args.mode == "rec":
       print("> MDOE: stream recorded audio")
       maxt = send_file_audio(queue, args.input_wav)

   streamer = send_live_audio(sock, queue, interrupt_event, vad_flag) #args.vad)
   streamer.start()

   print('*** Press CTRL-C to interrupt at any time ***\n\n\n')

   # Reader
   asr_text = reader(sock,interrupt_event,args.asr_mode,int(args.asr_nbest),int(args.log_flag))
   asr_text.start()

   if args.mode == "live":
       while True:
           try:
               time.sleep(1) #wait 1 second, then go back and ask if thread is still alive
           except KeyboardInterrupt: #if ctrl-C is pressed within that second,
                                     #catch the KeyboardInterrupt exception
               interrupt_event.set() #set the flag that will kill the thread when it has finished
               print('Exiting...')
               print('... recorder')
               #audio.join() #wait for the thread to finish
               print('... streamer')
               streamer.join() #wait for the thread to finish
               sock.shutdown(socket.SHUT_RDWR)
               print('... reader')
               asr_text.join()
               sock.close()
               print('done')
               exit()

   if args.mode == "rec":
       while asr_text.isAlive():
           try:
               time.sleep(1) #wait 1 second, then go back and ask if thread is still alive
               if asr_text.idur + 0.1 >= sent_audio_sec:
                   print("All processed!")
                   break
               #print(asr_text.idur, sent_audio_sec)
           except KeyboardInterrupt: #if ctrl-C is pressed within that second,
                                     #catch the KeyboardInterrupt exception
               break
       interrupt_event.set() #set the flag that will kill the thread when it has finished
       print('Exiting...')
       print('... reader')
       print('... streamer')
       streamer.join() #wait for the thread to finish
       sock.shutdown(socket.SHUT_RDWR)
       asr_text.join() #wait for the thread to finish
       sock.close()
               #exit(-1)
       exit()
   #return 0

if __name__ == "__main__":
   main()
