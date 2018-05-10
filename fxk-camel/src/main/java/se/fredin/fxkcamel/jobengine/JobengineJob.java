package se.fredin.fxkcamel.jobengine;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class JobengineJob extends RouteBuilder {

    @Autowired
    private SettingsComponent settingsComponent;

    public SettingsComponent getSettingsComponent() {
        return settingsComponent;
    }

    public void setSettingsComponent(SettingsComponent settingsComponent) {
        this.settingsComponent = settingsComponent;
    }
}
