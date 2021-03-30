package com.pedrocomitto.rabbit4lazy

import org.springframework.amqp.rabbit.core.RabbitTemplate

class GenericProducer(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitBindingProperties: RabbitBindingProperties
) {

     fun produce(bindingName: String, message: Any) {
        val rabbitBinding = rabbitBindingProperties.bindings[bindingName]
            ?: throw IllegalArgumentException("binding not found")

        rabbitTemplate.convertAndSend(rabbitBinding.exchangeName, rabbitBinding.queueRoutingKey, message)
    }

}