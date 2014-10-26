package kr.ac.korea.mobide.apiservice;

/**
 * Created by Koo Lee on 2014-08-29.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManagerFactory;

@ComponentScan
@EnableAutoConfiguration(exclude = {EntityManagerFactory.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
