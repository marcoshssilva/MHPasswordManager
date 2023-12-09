FROM docker.io/rabbitmq:3.10.23-management-alpine

COPY rabbitmq.conf /etc/rabbitmq
COPY definitions.json /etc/rabbitmq

RUN rabbitmq-plugins enable --offline rabbitmq_mqtt rabbitmq_federation_management rabbitmq_stomp
RUN cat /etc/rabbitmq/rabbitmq.conf