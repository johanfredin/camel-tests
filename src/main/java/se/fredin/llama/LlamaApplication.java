package se.fredin.llama;

import org.apache.camel.component.properties.PropertiesComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import se.fredin.llama.examples.Ex1_CSVList;
import se.fredin.llama.examples.Ex3_FilterValidateAgainst;

@SpringBootApplication
@ComponentScan
public class LlamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlamaApplication.class, args);
    }

//    @Bean
    public Ex1_CSVList ex1_csvList() {
        return new Ex1_CSVList();
    }

    @Bean
    public Ex3_FilterValidateAgainst ex3_filterValidateAgainst() {
        return new Ex3_FilterValidateAgainst();
    }

}
