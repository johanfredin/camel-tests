package se.fredin.fxkcamel.jobengine.task.join;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.mock.bean.JobEngineBean;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for joining 2 collections similar to how it was made in the fxk connector
 *
 * @author JFN
 */
public class JoinTask {

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

    public Exchange join() {
        Map<Object, List<JobEngineBean>> c1Beans = JobUtils.<JobEngineBean>asMap(getMainExchange());
        Map<Object, List<JobEngineBean>> c2Beans = JobUtils.<JobEngineBean>asMap(getJoiningExchange());

        List<JobEngineBean> result = c1Beans.entrySet()
                .stream()
                .filter(me -> handleMatch(me, c2Beans))                             // Filter depending on record selection
                .peek(me -> addData(me, c2Beans))                                   // Merge data from joining exchange
                .flatMap(listContainer -> listContainer.getValue().stream())
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
    private boolean handleMatch(Map.Entry<Object, List<JobEngineBean>> mapEntry, Map<Object, List<JobEngineBean>> c2Beans) {

        // When selection is all we always want all data regardless of match
        if(getRecordSelection() == RecordSelection.ALL) {
            return true;
        }

        // Keep record in collection if record selection one of: only_in_both, all AND out entity one of: both, entity_1
        if (c2Beans.containsKey(mapEntry.getKey())) {
            switch (getRecordSelection()) {
                case RECORDS_ONLY_IN_TYPE_1_AND_2:
                    return true;
            }
        }

        // Handle when no match found
        switch (getRecordSelection()) {
            case RECORDS_ONLY_IN_TYPE_1:
            case RECORDS_ONLY_IN_TYPE_2:
                return true;
        }
        return false;
    }

    private void addData(Map.Entry<Object,List<JobEngineBean>> me, Map<Object,List<JobEngineBean>> c2Beans) {

        // If we only want data from main exchange then no point in looking for data from joining exchange
        if(getOutEntity() == OutEntity.ENTITY_1) {
            return;
        }

    }

    @Override
    public String toString() {
        return "JoinTask{" +
                "mainExchange=" + mainExchange +
                ", joiningExchange=" + joiningExchange +
                ", recordSelection=" + recordSelection +
                ", outEntity=" + outEntity +
                '}';
    }
}
