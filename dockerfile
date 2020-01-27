# NOTE: make sure to have kafka downloaded at the same dir level as brevity

FROM openjdk

COPY . /root/brevity/
RUN mkdir /root/brevity/log

COPY kafka_2.12-2.4.0/ /root/kafka_2.12-2.2.0/

RUN yum install -y oracle-epel-release-el7
RUN yum install -y python36

RUN yum install -y gcc-c++ make
RUN curl -sL https://rpm.nodesource.com/setup_8.x | bash -
RUN yum install -y nodejs

RUN yum install -y git
RUN yum install -y vi
RUN yum install -y firefox


CMD tail -f /dev/null
