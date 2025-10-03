package site.alphacode.alphacodepaymentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("payment.exchange");
    }

    // --- Course queue ---
    @Bean
    public Queue courseQueue() {
        return new Queue("course.create.queue", true);
    }

    @Bean
    public Binding bindingCourseQueue(Queue courseQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(courseQueue).to(paymentExchange).with("course.create.queue");
    }

    // --- Bundle queue ---
    @Bean
    public Queue bundleQueue() {
        return new Queue("bundle.create.queue", true);
    }

    @Bean
    public Binding bindingBundleQueue(Queue bundleQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(bundleQueue).to(paymentExchange).with("bundle.create.queue");
    }

    // --- Addon queue ---
    @Bean
    public Queue addonQueue() {
        return new Queue("addon.create.queue", true);
    }

    @Bean
    public Binding bindingAddonQueue(Queue addonQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(addonQueue).to(paymentExchange).with("addon.create.queue");
    }

    // --- Subscription queue ---
    @Bean
    public Queue subscriptionQueue() {
        return new Queue("subscription.create.queue", true);
    }

    @Bean
    public Binding bindingSubscriptionQueue(Queue subscriptionQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(subscriptionQueue).to(paymentExchange).with("subscription.create.queue");
    }

    // --- Email queue ---
    @Bean
    public Queue emailQueue() {
        return new Queue("email.send.queue", true);
    }

    @Bean
    public Binding bindingEmailQueue(Queue emailQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(emailQueue).to(paymentExchange).with("email.send.queue");
    }
}


