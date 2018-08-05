package se.fredin.llama.examples;

import org.wiztools.xsdgen.ParseException;
import org.wiztools.xsdgen.XsdGen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XsdGenTest {

    public static void main(String... args) {
        var gen = new XsdGen();
        try {
            gen.parse(new File("src/test/resources/input/foo.xml"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        var out = new File("src/test/resources/input/foo.xsd");
        try {
            gen.write(new FileOutputStream(out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
