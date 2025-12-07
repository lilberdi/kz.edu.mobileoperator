package kz.edu.mobileoperator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Mobile Operator Tariff Management application.
 * <p>
 * This is a standard Spring Boot bootstrap class. It starts the embedded
 * application server (Tomcat by default) and initializes the Spring context.
 */
@SpringBootApplication
public class MobileOperatorTariffManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileOperatorTariffManagementApplication.class, args);
    }
}



