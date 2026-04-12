package com.devsu.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the customer microservice. */
@SpringBootApplication
public class CustomerServiceApplication {

  /** Runs the Spring Boot application. */
  public static void main(String[] args) {
    SpringApplication.run(CustomerServiceApplication.class, args);
  }
}
