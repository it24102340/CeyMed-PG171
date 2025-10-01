package com.healthinsurancemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.healthinsurancemanagementsystem",

})
public class CeyMedInsuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CeyMedInsuranceApplication.class, args);
    }
}


