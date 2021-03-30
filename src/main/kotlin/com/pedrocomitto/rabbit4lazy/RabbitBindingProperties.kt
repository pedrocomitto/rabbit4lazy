package com.pedrocomitto.rabbit4lazy

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("spring.rabbitmq")
data class RabbitBindingProperties(
    val bindings: HashMap<String, RabbitBinding>
) {
    init {
        bindings.entries.forEach { binding ->
            if (binding.value.autoGenerate) {
                binding.value.let {
                    val key = binding.key

                    RabbitBinding.of(
                        exchangeName = "$key.exchange",
                        queueName = "$key.queue",
                        dlqName = "$key.queue.dlq"
                    ).let { binding.setValue(it) }
                }
            }
        }
    }
}

class RabbitBinding private constructor(
    val autoGenerate: Boolean = false,
    exchangeName: String?,
    queueName: String?,
    dlqName: String?
) {
    val exchangeName: String
    val queueName: String
    val dlqName: String
    val queueRoutingKey: String

    init {
        if (!autoGenerate) {
            this.exchangeName = exchangeName ?: throw IllegalArgumentException("Property spring.rabbitmq.bindings.exchangeName is missing")
            this.queueName = queueName ?: throw IllegalArgumentException("Property spring.rabbitmq.bindings.queueName is missing")
            this.dlqName = dlqName ?: throw IllegalArgumentException("Property spring.rabbitmq.bindings.dlqName is missing")
            this.queueRoutingKey = queueName
        } else {
            this.exchangeName = ""
            this.queueName = ""
            this.dlqName = ""
            this.queueRoutingKey = ""
        }
    }

    companion object {
        fun of(exchangeName: String, queueName: String, dlqName: String): RabbitBinding {
            return RabbitBinding(false, exchangeName, queueName, dlqName)
        }
    }

    override fun toString(): String {
        return "RabbitBinding(exchangeName='$exchangeName', queueName='$queueName', dlqName='$dlqName', queueRoutingKey='$queueRoutingKey')"
    }


}
