package com.x.agile.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@ComponentScan
public class AgileD365Scheduler
  extends SpringBootServletInitializer
{
  public static void main(String[] args)
  {
    SpringApplication.run(AgileD365Scheduler.class, args);
  }
  
  @Bean
  public RestTemplate restTemplate()
  {
    return new RestTemplate();
  }
}

