FROM ubuntu:20.04

COPY . /root
WORKDIR /root
RUN bash install.sh
EXPOSE 8250

ENTRYPOINT ["bash","start.sh"]
