package com.pedrocomitto.rabbit4lazy

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("spring.rabbitmq")
data class RabbitBindingProperties(
    val bindings: HashMap<String, RabbitBinding>
)

@ConstructorBinding
data class RabbitBinding(
    val exchangeName: String,
    val queueName: String,
    val dlqName: String
) {
    val queueRoutingKey = this.queueName
}