package com.example.receitas.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.receitas.*"})
public class ReceitasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceitasApplication.class, args);
    }
}