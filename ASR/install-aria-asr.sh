#!/bin/bash

#
# if this fails to run, ensure that it's got Unix style line endings and git isn't breaking it
#

MODELSEN="ARIA-ASR-English.tar.gz"
MODELSDE="ARIA-ASR-German.tar.gz"
MODELSFR="ARIA-ASR-French.tar.gz"
MODELDATE="2017-10-17"
HOSTURL="https://noxi.aria-agent.eu/models"
TARGETDIREN="./models/en/"
TARGETDIRDE="./models/de/"
TARGETDIRFR="./models/fr/"
printf "\n\n------------------------\n* Installing English models ... \n........................\n"
wget "$HOSTURL/$MODELDATE/$MODELSEN"
rm -rf $TARGETDIREN
mkdir -p $TARGETDIREN
tar -xf $MODELSEN -C "./models/en/"
rm $MODELSEN
printf "\n\n------------------------\n*Installing German models ... \n........................\n"
wget "$HOSTURL/$MODELDATE/$MODELSDE"
rm -rf $TARGETDIRDE
mkdir -p $TARGETDIRDE
tar -xf $MODELSDE -C "./models/de/"
rm $MODELSDE
printf "\n\n------------------------\n*Installing French models ... \n........................\n"
wget "$HOSTURL/$MODELDATE/$MODELSFR"
rm -rf $TARGETDIRFR
mkdir -p $TARGETDIRFR
tar -xf $MODELSFR -C "./models/fr/"
rm $MODELSFR
#printf "\n\n------------------------\n*Copying 'atlas' folder to '/usr/local/' ... \n"
#sudo cp -r "atlas" "/usr/local/"
printf "\n\n------------------------\n*Install libatlas3-base ... \n"
sudo apt install libatlas3-base
# Uncomment the next two lines if you need to install these libraries
# (depending on your platform you may need 5.5 or 7.5)
#printf "\n\n------------------------\n*Install libcublas, libcurand, and libcudart ... \n"
#sudo apt install libcublas5.5 libcurand5.5 libcudart5.5
#sudo apt install libcublas7.5 libcurand7.5 libcudart7.5
printf "\n\n------------------------\n*Making binaries executable ... \n"
chmod +x bin/online2-audio-nnet2-latgen-faster
chmod +x bin/online2-audio-nnet2-latgen-faster-gpu
printf "\n\n------------------------\n* Installation finished.\n\n"
