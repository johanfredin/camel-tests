package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.bean.CsvUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Receive input data containing users with properties
 * - first name
 * - last name
 * - age
 * - gender
 * - country
 * <p>
 * * Filter out invalid age (<1 and/or >100)
 * * Group by country
 * * Create output data
 */
@Component
public class Ex1_CSV extends JobengineJob {

    @Override
    public void configure() throws Exception {
        BindyCsvDataFormat bindyCsvDataFormat = new BindyCsvDataFormat(CsvUser.class);

        from(JobUtils.getInputFile(getSettingsComponent().getInputDirectory(), "foo.csv"))
                .unmarshal(bindyCsvDataFormat)
                .process(e -> filterAge(e))
                .process(e -> sortOnCountry(e))
                .marshal(bindyCsvDataFormat)
                .to(JobUtils.getInputFile(getSettingsComponent().getOutputDirectory(), "foo_fixed.csv"));
    }

    private void filterAge(Exchange exchange) {
        List<CsvUser> users = ((List<CsvUser>) exchange.getIn().getBody())
            .stream()
                .filter(user -> user.getAge() > 0 && user.getAge() < 100)
                .collect(Collectors.toList());

        // Update the body
        exchange.getIn().setBody(users);
    }

    private void sortOnCountry(Exchange e) {
        List<CsvUser> users = ((List<CsvUser>) e.getIn().getBody())
                .stream()
                .sorted(Comparator.comparing(o -> o.getCountry()))
                .collect(Collectors.toList());

        e.getIn().setBody(users);
    }



}
