package br.com.marcoshssilva.mhpasswordmanager.passwordservice.configs;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.amqp.queues.FileEncryptionWorkerQueue;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue encryptionCompletedQueue() {
        return new Queue(FileEncryptionWorkerQueue.ENCRYPTION_COMPLETED, true);
    }

    @Bean
    public Queue encryptionFailedQueue() {
        return new Queue(FileEncryptionWorkerQueue.ENCRYPTION_FAILED, true);
    }

    @Bean
    public Queue encryptionRequestedQueue() {
        return new Queue(FileEncryptionWorkerQueue.ENCRYPTION_REQUESTED, true);
    }

    @Bean
    public TopicExchange filesEventsExchange() {
        return new TopicExchange(FileEncryptionWorkerQueue.EXCHANGE);
    }

    @Bean
    public Binding encryptionCompletedBinding(Queue encryptionCompletedQueue, TopicExchange filesEventsExchange) {
        return BindingBuilder.bind(encryptionCompletedQueue).to(filesEventsExchange).with(FileEncryptionWorkerQueue.ENCRYPTION_COMPLETED);
    }

    @Bean
    public Binding encryptionFailedBinding(Queue encryptionFailedQueue, TopicExchange filesEventsExchange) {
        return BindingBuilder.bind(encryptionFailedQueue).to(filesEventsExchange).with(FileEncryptionWorkerQueue.ENCRYPTION_FAILED);
    }

    @Bean
    public Binding encryptionRequestedBinding(Queue encryptionRequestedQueue, TopicExchange filesEventsExchange) {
        return BindingBuilder.bind(encryptionRequestedQueue).to(filesEventsExchange).with(FileEncryptionWorkerQueue.ENCRYPTION_REQUESTED);
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
