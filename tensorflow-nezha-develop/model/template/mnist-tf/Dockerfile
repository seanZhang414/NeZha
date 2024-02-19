FROM tensorflow/tensorflow:1.5.0-devel-gpu
RUN mkdir /app
WORKDIR /app
RUN mkdir ./logs
RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/' /etc/apt/sources.list
COPY pipconf /root/.pip/pip.conf


COPY ./* /app/


