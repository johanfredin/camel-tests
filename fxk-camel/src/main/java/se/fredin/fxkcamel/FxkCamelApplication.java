package se.fredin.fxkcamel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

}
