package com.nttdata.banking.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Account Service.
 */
@SpringBootApplication(scanBasePackages = {
        "com.nttdata.banking.account",
        "com.nttdata.banking.customer.client"
})
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
