package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.examples.bean.CsvUser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input/output is csv
 */
public class Ex1_CSV extends JobengineJob {



    @Override
    public void configure() {

        BindyCsvDataFormat bindyCsvDataFormat = new BindyCsvDataFormat(CsvUser.class);

        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "foo.csv"))                           // Fetch input file
                .unmarshal(bindyCsvDataFormat)                                                                     // Unmarshal CSV to POJO
                .process(e -> processUsers(e))                                                               // Do transformation
                .marshal(bindyCsvDataFormat)                                                                 // Marshal POJO back to CSV
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "foo_fixed.csv")).stop();    // Write output file

    }

    private void processUsers(Exchange exchange) {
        List<CsvUser> users = JobUtils.<CsvUser>asList(exchange)
                .stream()                                                       // Iterate users
                .filter(user -> user.getAge() > 0 && user.getAge() < 100)       // Filter out invalid age
                .sorted(Comparator.comparing(user -> user.getCountry()))        // Sort on country
                .peek(user -> user.setGender(user.getGender().toUpperCase()))   // Set gender to be uppercase
                .collect(Collectors.toList());                                  // Collect the update

        // Update the body
        exchange.getIn().setBody(users);
    }


}
