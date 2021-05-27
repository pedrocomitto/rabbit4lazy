# rabbit4lazy

### The l̶a̶z̶i̶e̶s̶t̶ fastest way to set up RabbitMQ in your Spring Boot application

### Install

You don't even need to manually add Spring AMQP. The library take care of everything for you.

#### Maven
``` 
<dependency>
    <groupId>com.github.pedrocomitto</groupId>
    <artifactId>rabbit4lazy</artifactId>
    <version>1.0.0</version>
</dependency> 
```

#### Gradle
``` implementation 'com.github.pedrocomitto:rabbit4lazy:1.0.0' ```

### Usage

#### Configure

In addition to the RabbitMQ properties, just add ``` spring.rabbitmq.bindings.<binding-name>.autoGenerate=true ``` to your application.properties and it's done!

Example using "send-notification" as binding:

```
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    bindings: <------- rabbit4lazy binding
      send-notification: <----- example binding name
        autoGenerate: true <---- automatically generates an exchange, a queue and a DLQ
```

or

```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.bindings.send-notification.autoGenerate=true
```

With the above properties, it will automatically generate:

send-notification.exchange \
send-notification.queue \
send-notification.queue.dlq 

#### Produce

Now you can use a Spring Bean called "GenericProducer" to send data:

```genericProducer.produce(String bindingName, Object message)```

Example:

``` 
@Service
public class MyService {

    // GenericProducer bean
    private final GenericProducer genericProducer;
    
    // constructor injection
    public MyService(GenericProducer genericProducer) {
        this.genericProducer = genericProducer;
    }

    // method receiving an example object "Notification" and sending it to RabbitMQ
    public void myExampleMethod(final Notification notification) {
        genericProducer.produce("send-notification", notification);
    }
} 
```

The above example, will send a message to the "send-notification.exchange".

### Other customizations

Alternatively, you can define your own exchange, queue and dlq names:

```
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    bindings: <------- rabbit4lazy binding
      send-notification: <----- example binding name
        exchangeName: send-sms.exchange
        queueName: send-sms.queue
        dlqName: send-sms.queue.dlq
```

or

```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.bindings.send-notification.exchangeName=send-sms.exchange
spring.rabbitmq.bindings.send-notification.queueName=send-sms.queue
spring.rabbitmq.bindings.send-notification.dlqName=send-sms.queue.dlq
```
