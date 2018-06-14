package se.fredin.fxkcamel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import se.fredin.fxkcamel.jobengine.examples.Ex1_CSV;
import se.fredin.fxkcamel.jobengine.examples.Ex1_JSON;
import se.fredin.fxkcamel.jobengine.examples.Ex1_XML;
import se.fredin.fxkcamel.jobengine.examples.Ex2_2CSVTo1JSON;

@SpringBootApplication
@ComponentScan
public class FxkCamelApplication {

    public static void main(String[] args) {
        SpringApplication.run(FxkCamelApplication.class, args);
    }

//    @Bean
    public Ex1_CSV ex1_CSV() {
        return new Ex1_CSV();
    }

//    @Bean
    public Ex1_JSON ex1_JSON() {
        return new Ex1_JSON();
    }

//    @Bean
    public Ex1_XML ex1_XML() {
        return new Ex1_XML();
    }

//    @Bean
    public Ex2_2CSVTo1JSON ex2_2CSVTo1JSON() {
        return new Ex2_2CSVTo1JSON();
    }

}
