package site.alphacode.alphacodepaymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
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

    // --- Notification queue ---
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.send.queue", true);
    }

    @Bean
    public Binding bindingNotificationQueue(Queue notificationQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(notificationQueue).to(paymentExchange).with("notification.send.queue");
    }

    // --- JSON Converter (fix lỗi conversion) ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
