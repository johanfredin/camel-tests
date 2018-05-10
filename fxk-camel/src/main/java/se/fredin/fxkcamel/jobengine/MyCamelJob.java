package se.fredin.fxkcamel.jobengine;

import org.springframework.stereotype.Component;

@Component
public class MyCamelJob extends JobengineJob {

    @Override
    public void configure() {
        /*
        from("file:" + getSettingsComponent().getInputDirectory() + "?noop=true")
                .to("file:" + getSettingsComponent().getOutputDirectory());
                */
    }
}
