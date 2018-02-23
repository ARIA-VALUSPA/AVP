

Directory contents
-------------------

This directory contains all the ASR models necessary to run the
scripts in the run dir. These models are created as a result of
a training phase performed via Kaldi using the online-nnet2 recipe
with "multi-splice". The same files should be stored under every
dir dedicated for every language (i.e. en, de, and fr). Note that
there are no files in this folder. See our WIKI page for instructions
on how to obtain all the necessary files.


final.dubm:

	Diagonal universal background model (UBM) for the ivector extractor.

final.ie:

	The ivector extractor model.

final.mat:

	The LDA matrix.

final.mdl:

	The neural network model (nnet2 model).

global_cmvn.stats:

	The global cepstral mean and variance normalization statistics.

HCLG.fst:

	The pre-compiled FST decoding graph

words.txt:

	The map table between words and word-ids
