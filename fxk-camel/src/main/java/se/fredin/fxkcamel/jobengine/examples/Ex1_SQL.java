package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;
import se.fredin.fxkcamel.jobengine.bean.CsvUser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input is SQL
 */
@Component
public class Ex1_SQL extends JobengineJob {

    @Override
    public void configure() {
        from(JobUtils.sql("select * from user", CsvUser.class))
                .process(e -> processUsers(e))
                .marshal(new BindyCsvDataFormat(CsvUser.class))
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "foo_fixed.sql"))
                .end();
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
