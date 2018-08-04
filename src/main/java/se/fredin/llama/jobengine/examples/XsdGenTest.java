package se.fredin.llama.jobengine.examples;

import org.wiztools.xsdgen.ParseException;
import org.wiztools.xsdgen.XsdGen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class XsdGenTest {

    public static void main(String... args) {
        XsdGen gen = new XsdGen();
        try {
            gen.parse(new File("src/test/resources/input/foo.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        File out = new File("src/test/resources/input/foo.xsd");
        try {
            gen.write(new FileOutputStream(out));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
