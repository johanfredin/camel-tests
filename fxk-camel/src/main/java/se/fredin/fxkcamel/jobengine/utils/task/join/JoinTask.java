package se.fredin.fxkcamel.jobengine.utils.task.join;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.bean.JobEngineBean;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Used for joining 2 collections similar to how it was made in the fxk connector
 *
 * @Author JFN
 */
public class JoinTask<T1 extends JobEngineBean, T2 extends JobEngineBean> {

    private Exchange mainExchange;
    private Exchange joiningExchange;
    private RecordSelection recordSelection;
    private OutEntity outEntity;

    public JoinTask(Exchange mainExchange, Exchange joiningExchange, RecordSelection recordSelection, OutEntity outEntity) {
        setMainExchange(mainExchange);
        setJoiningExchange(joiningExchange);
        setRecordSelection(recordSelection);
        setOutEntity(outEntity);
    }

    public Exchange getMainExchange() {
        return mainExchange;
    }

    public void setMainExchange(Exchange mainExchange) {
        this.mainExchange = mainExchange;
    }

    public Exchange getJoiningExchange() {
        return joiningExchange;
    }

    public void setJoiningExchange(Exchange joiningExchange) {
        this.joiningExchange = joiningExchange;
    }

    public RecordSelection getRecordSelection() {
        return recordSelection;
    }

    public void setRecordSelection(RecordSelection recordSelection) {
        this.recordSelection = recordSelection;
    }

    public OutEntity getOutEntity() {
        return outEntity;
    }

    public void setOutEntity(OutEntity outEntity) {
        this.outEntity = outEntity;
    }

    public Exchange join(Class<T1> c1, Class<T2> c2) {
        Map<Object, List<T1>> c1Beans = JobUtils.<T1>asMap(this.mainExchange);
        Map<Object, List<T2>> c2Beans = JobUtils.<T2>asMap(this.joiningExchange);

        Map<Object, List<T1>> collect = c1Beans.entrySet()
                .stream()
                .filter(me -> handleMatch(me, c2Beans))                             // Filter depending on record selection
                .peek(me -> addData(me, c2Beans))                                   // Merge data from joining exchange
                .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));     // Collect the result

        // Merge the list values into one
        List<T1> result = collect.values()
                .stream()
                .flatMap(listContainer -> listContainer.stream())
                .collect(Collectors.toList());

        // Update the body with the result
        this.mainExchange.getIn().setBody(result);

        return this.mainExchange;

    }

    /**
     * Filter depending on {@link #getRecordSelection()}
     * Keep record in collection if record selection if match found and selection one of: only_in_both, all
     *
     * @param mapEntry
     * @param c2Beans
     * @return
     */
    private boolean handleMatch(Map.Entry<Object, List<T1>> mapEntry, Map<Object, List<T2>> c2Beans) {

        // When selection is all we always want all data regardless of match
        if(this.recordSelection == RecordSelection.ALL) {
            return true;
        }

        // Keep record in collection if record selection one of: only_in_both, all AND out entity one of: both, entity_1
        if (c2Beans.containsKey(mapEntry.getKey())) {
            switch (this.recordSelection) {
                case RECORDS_ONLY_IN_TYPE_1_AND_2:
                    return true;
            }
        }

        // Handle when no match found
        switch (this.recordSelection) {
            case RECORDS_ONLY_IN_TYPE_1:
            case RECORDS_ONLY_IN_TYPE_2:
                return true;
        }
        return true;
    }

    private void addData(Map.Entry<Object,List<T1>> me, Map<Object,List<T2>> c2Beans) {

        // If we only want data from main exchange then no point in looking for data from joining exchange
        if(this.outEntity == OutEntity.ENTITY_1) {
            return;
        }

    }

}
