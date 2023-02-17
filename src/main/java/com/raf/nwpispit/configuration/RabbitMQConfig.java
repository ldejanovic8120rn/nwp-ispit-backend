package com.raf.nwpispit.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue machineQueue() {
        return new Queue("machineQueue", false);
    }

}
