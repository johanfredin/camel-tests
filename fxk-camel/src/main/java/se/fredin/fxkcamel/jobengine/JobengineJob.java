package se.fredin.fxkcamel.jobengine;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.utils.SettingsComponent;

public abstract class JobengineJob extends RouteBuilder {

    @Autowired
    private SettingsComponent settingsComponent;

    public SettingsComponent getSettingsComponent() {
        return settingsComponent;
    }

    public void setSettingsComponent(SettingsComponent settingsComponent) {
        this.settingsComponent = settingsComponent;
   }

   public String prop(String property) {
        return "{{" + property + "}}";
   }
}


