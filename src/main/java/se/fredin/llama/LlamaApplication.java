package se.fredin.llama;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class LlamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlamaApplication.class, args);
    }

}
