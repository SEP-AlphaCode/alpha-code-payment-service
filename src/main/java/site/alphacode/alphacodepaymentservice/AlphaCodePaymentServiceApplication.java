package site.alphacode.alphacodepaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@EnableScheduling
@SpringBootApplication
@EnableCaching
public class AlphaCodePaymentServiceApplication {

    private final Environment environment;

    public AlphaCodePaymentServiceApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(AlphaCodePaymentServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationPort() {
        String port = environment.getProperty("local.server.port");
        String address = environment.getProperty("server.address", "localhost"); // default to localhost

        String contextPath = environment.getProperty("server.servlet.context-path", "");

        String url = "http://" + address + ":" + port + contextPath + "/swagger";
        System.out.println("Application started at: " + url);
    }

}
