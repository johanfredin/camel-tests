package se.fredin.llama.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import se.fredin.llama.jobengine.JobengineJob;
import se.fredin.llama.jobengine.examples.bean.CsvUser;
import se.fredin.llama.jobengine.utils.JobUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Doing ex.1 where input/output is JSON
 */
public class Ex1_JSON extends JobengineJob {

    @Override
    public void configure() {
        from(JobUtils.file(getSettingsComponent().getInputDirectory(), "foo.json"))
                .unmarshal(new ListJacksonDataFormat(CsvUser.class))
                .process(e -> processUsers(e))
                .marshal().json(JsonLibrary.Jackson)
                .to(JobUtils.file(getSettingsComponent().getOutputDirectory(), "foo_fixed.json"));
    }

    private void processUsers(Exchange exchange) {
        List<CsvUser> users = JobUtils.<CsvUser>asFxkBeanList(exchange)
                .stream()                                                       // Iterate users
                .filter(user -> user.getAge() > 0 && user.getAge() < 100)       // Filter out invalid age
                .sorted(Comparator.comparing(user -> user.getCountry()))        // Sort on country
                .peek(user -> user.setGender(user.getGender().toUpperCase()))   // Set gender to be uppercase
                .collect(Collectors.toList());                                  // Collect the update

        // Update the body
        exchange.getIn().setBody(users);
    }

}
