package se.fredin.fxkcamel.job;

import org.springframework.stereotype.Component;

@Component
public class JobRouterComponent extends JobengineJob {

    @Override
    public void configure() {
        from("file:" + getSettingsComponent().getInputDirectory() + "?noop=true")
                .to("file:" + getSettingsComponent().getOutputDirectory());
    }
}
