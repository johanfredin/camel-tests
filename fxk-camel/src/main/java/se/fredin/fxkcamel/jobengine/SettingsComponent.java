package se.fredin.fxkcamel.jobengine;

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

    @Value("${db-connection-string}")
    private String dbConnectionString;

    @Value("${db-user}")
    private String dbUser;

    @Value("${db-password}")
    private String dbPassword;

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

    public String getDbConnectionString() {
        return dbConnectionString;
    }

    public void setDbConnectionString(String dbConnectionString) {
        this.dbConnectionString = dbConnectionString;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
