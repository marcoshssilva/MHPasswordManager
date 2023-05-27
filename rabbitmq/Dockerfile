FROM 3.10.23-management-alpine
#ENV RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS -rabbitmq_management load_definitions "/tmp/rabbit.definitions.json"
#WORKDIR tmp
#COPY ./rabbit.definitions.json /
RUN rabbitmq-plugins enable --offline rabbitmq_mqtt rabbitmq_federation_management rabbitmq_stomp
