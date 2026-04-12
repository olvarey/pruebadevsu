package com.devsu.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the account microservice. */
@SpringBootApplication
public class AccountServiceApplication {

  /** Runs the Spring Boot application. */
  public static void main(String[] args) {
    SpringApplication.run(AccountServiceApplication.class, args);
  }
}
