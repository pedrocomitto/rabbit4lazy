package com.pedrocomitto.rabbit4lazy

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [RabbitBindingProperties::class])
class Rabbit4LazyAutoConfiguration {

    @Bean
    fun rabbitAdmin(rabbitTemplate: RabbitTemplate, rabbitBindingProperties: RabbitBindingProperties): RabbitAdmin {
        val rabbitAdmin = RabbitAdmin(rabbitTemplate)

        rabbitTemplate.isChannelTransacted = true

        rabbitBindingProperties.bindings.values.forEach { binding ->
            val exchange = TopicExchange(binding.exchangeName, true, false)
            rabbitAdmin.declareExchange(exchange)

            val queue = QueueBuilder.durable(binding.queueName)
                .deadLetterExchange(binding.exchangeName)
                .deadLetterRoutingKey(binding.dlqName)
                .build()
            rabbitAdmin.declareQueue(queue)

            val dlq = QueueBuilder.durable(binding.dlqName).build()
            rabbitAdmin.declareQueue(dlq)

            val queueBinding = BindingBuilder.bind(queue).to(exchange).with(binding.queueName)
            rabbitAdmin.declareBinding(queueBinding)

            val dlqBinding = BindingBuilder.bind(dlq).to(exchange).with(binding.dlqName)
            rabbitAdmin.declareBinding(dlqBinding)
        }

        return rabbitAdmin
    }

    @Bean
    fun genericProducer(rabbitTemplate: RabbitTemplate, rabbitBindingProperties: RabbitBindingProperties) =
        GenericProducer(rabbitTemplate, rabbitBindingProperties)

    @Bean
    fun messageConverter(objectMapper: ObjectMapper): MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }

}
