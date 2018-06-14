package se.fredin.fxkcamel.jobengine.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SettingsComponent {

    @Value("${input-directory}")
    private String inputDirectory;

    @Value("${output-directory}")
    private String outputDirectory;

    @Value("${schema-directory}")
    private String xmlSchemaDirectory;

    public String getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getXmlSchemaDirectory() {
        return xmlSchemaDirectory;
    }

    public void setXmlSchemaDirectory(String xmlSchemaDirectory) {
        this.xmlSchemaDirectory = xmlSchemaDirectory;
    }

}
