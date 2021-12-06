FROM ubuntu:20.04

COPY . /root
WORKDIR /root
RUN bash install.sh
EXPOSE 20200

ENTRYPOINT ["bash","/root/start.sh"]
