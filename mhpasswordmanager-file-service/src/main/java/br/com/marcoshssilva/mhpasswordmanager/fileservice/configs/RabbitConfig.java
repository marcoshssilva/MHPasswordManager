package br.com.marcoshssilva.mhpasswordmanager.fileservice.configs;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import br.com.marcoshssilva.mhpasswordmanager.fileservice.amqp.queues.FileProcessingWorkerQueue;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue encryptionCompletedQueue() {
        return new Queue(FileProcessingWorkerQueue.ENCRYPTION_COMPLETED, true);
    }

    @Bean
    public Queue encryptionFailedQueue() {
        return new Queue(FileProcessingWorkerQueue.ENCRYPTION_FAILED, true);
    }

    @Bean
    public Queue encryptionRequestedQueue() {
        return new Queue(FileProcessingWorkerQueue.ENCRYPTION_REQUESTED, true);
    }

    @Bean
    public MessageConverter converter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    @Bean
    @Primary
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }
}
