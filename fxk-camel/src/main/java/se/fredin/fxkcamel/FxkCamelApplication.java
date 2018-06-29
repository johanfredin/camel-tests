package se.fredin.fxkcamel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import se.fredin.fxkcamel.jobengine.examples.*;

@SpringBootApplication
@ComponentScan
public class FxkCamelApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxkCamelApplication.class, args);
    }

    @Bean
    public Ex1_CSVList ex1_csvList() {
        return new Ex1_CSVList();
    }


}
