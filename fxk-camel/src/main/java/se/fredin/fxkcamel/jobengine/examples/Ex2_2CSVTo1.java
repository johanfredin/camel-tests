package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.bean.Pet;
import se.fredin.fxkcamel.jobengine.bean.User;

/**
 * Read 2 csv files and join them into one
 */
public class Ex2_2CSVTo1 extends JobengineJob {


    @Override
    public void configure() throws Exception {
        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "person .csv"))
                .unmarshal(new BindyCsvDataFormat(User.class))
                .to("endpoint:user");

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "pet .csv"))
                .unmarshal(new BindyCsvDataFormat(Pet.class))
                .to("endpoint:pet");

        from("endpoint:user")
                .agg

    }
}
