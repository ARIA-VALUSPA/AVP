

# setup the system
FROM ubuntu
	LABEL maintainer="peter.lavalle@nottingham.ac.uk"
	ARG DEBIAN_FRONTEND=noninteractive
	RUN apt-get update && apt-get install -y dialog apt-utils
	RUN apt-get update && apt-get upgrade -y && apt-get install -y \
		libatlas-base-dev \
		sudo \
		wget
		
# create a user
USER root
	RUN mkdir /var/ariaasr_home
	RUN adduser --system --home /var/ariaasr_home --group --disabled-login ariaasr
	RUN chown -R ariaasr:ariaasr /var/ariaasr_home
	RUN echo 'ariaasr ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers.d/README

# setup the ASR system
USER ariaasr
	WORKDIR /var/ariaasr_home
	COPY --chown=ariaasr:ariaasr . /var/ariaasr_home
	RUN chmod +=rx ./install-aria-asr.sh
	RUN ./install-aria-asr.sh
	RUN chmod +=rx run/launch.sh
	RUN chmod +=rx run/launch-asr.sh


# run the ASR server
WORKDIR /var/ariaasr_home/run
	# no GPUs in docker
	RUN sed -i  's/gpu=1/gpu=0/g' /var/ariaasr_home/run/launch.sh
	RUN sed -i  's/gpu=1/gpu=0/g' /var/ariaasr_home/run/launch-asr.sh

	# launch it
	CMD /var/ariaasr_home/run/launch.sh
