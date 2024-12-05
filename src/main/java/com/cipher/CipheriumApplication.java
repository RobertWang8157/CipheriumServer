package com.cipher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "com.cipher")
public class CipheriumApplication {

    public static void main(String[] args) {
        SpringApplication.run(CipheriumApplication.class, args);
    }

    @Component
    public static class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            ApplicationContext applicationContext = event.getApplicationContext();

            // Assuming the application is running on a web server
            String serverPort = applicationContext.getEnvironment().getProperty("server.port");

            // You may need to adjust this based on your server configuration
            String contextPath = applicationContext.getEnvironment().getProperty("server.servlet.context-path", "");

            String baseUrl = "http://localhost:" + serverPort + contextPath;

            System.out.println("Application started. Access URLs:\n" +
                    "Local: \t\t" + baseUrl + "\n" +
                    "External: \t" + baseUrl);
        }
    }

}
