package se.fredin.fxkcamel.jobengine.examples;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ValueBuilder;
import org.springframework.stereotype.Component;
import se.fredin.fxkcamel.jobengine.JobUtils;
import se.fredin.fxkcamel.jobengine.JobengineJob;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
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
        from(JobUtils.getInputFile(getSettingsComponent().getInputDirectory(), "foo.csv"))
                .unmarshal().csv()
                .process(e -> filterAge(e))
                .process(e -> sortOnCountry(body()))
                .to(JobUtils.getInputFile(getSettingsComponent().getOutputDirectory(), "foo_fixed.csv"));
    }

    private void sortOnCountry(ValueBuilder e) {
        List<List<String>> users = (List<List<String>>) e;
        users.stream()
                .sorted(Comparator.comparing(o -> o.get(4)));
    }

    private void filterAge(Exchange exchange) {
        List<List<String>> users = (List<List<String>>) exchange.getIn().getBody();
        List<List<String>> filteredUsers = users.stream()
                .filter(
                        user -> user.stream()
                                .anyMatch(withinRange(Integer.parseInt(user.get(2))))
                ).collect(Collectors.toList());
        exchange.getIn().setBody(filteredUsers);
    }

    private Predicate<? super String> withinRange(int age) {
        return (s) -> age > 0 && age < 100;
    }


}
