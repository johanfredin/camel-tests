package se.fredin.fxkcamel.job;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import se.fredin.fxkcamel.settings.SettingsComponent;

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
