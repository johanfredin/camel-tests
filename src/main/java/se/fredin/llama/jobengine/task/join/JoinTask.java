package se.fredin.llama.jobengine.task.join;

import org.apache.camel.Exchange;
import se.fredin.llama.jobengine.task.BaseTask;
import se.fredin.llama.jobengine.task.Field;
import se.fredin.llama.jobengine.task.Fields;
import se.fredin.llama.jobengine.utils.JobUtils;

import java.util.*;

/**
 * Used for joining 2 collections similar to how it was made in the fxk connector
 *
 * @author JFN
 */
public class JoinTask extends BaseTask {

    private Exchange mainExchange;
    private Exchange joiningExchange;
    private JoinType joinType;

    private Fields entity1Fields;
    private Fields entity2Fields;

    private List<JoinKey> joinKeys;

    public JoinTask() {
        super();
    }

    public JoinTask(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> joinKeys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        super();
        setMainExchange(mainExchange);
        setJoiningExchange(joiningExchange);
        setJoinKeys(joinKeys);
        setJoinType(joinType);
        setEntity1Fields(entity1Fields);
        setEntity2Fields(entity2Fields);
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

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public Fields getEntity1Fields() {
        return entity1Fields;
    }

    public void setEntity1Fields(Fields entity1Fields) {
        this.entity1Fields = entity1Fields;
    }

    public Fields getEntity2Fields() {
        return entity2Fields;
    }

    public void setEntity2Fields(Fields entity2Fields) {
        this.entity2Fields = entity2Fields;
    }

    public List<JoinKey> getJoinKeys() {
        return joinKeys;
    }

    public void setJoinKeys(List<JoinKey> joinKeys) {
        this.joinKeys = joinKeys;
    }

    @Override
    public Exchange doExecuteTask() {
        List<Map<String, String>> main = JobUtils.asList(this.mainExchange);
        List<Map<String, String>> joining = JobUtils.asList(this.joiningExchange);

        // Make sure keys exist
        Set<String> mainKeys = main.get(0).keySet();
        Set<String> joiningKeys = joining.get(0).keySet();

        for (JoinKey joinKey : this.joinKeys) {
            if (!mainKeys.contains(joinKey.getKeyInMain())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in main exchange! Available keys in main are: " + Arrays.toString(mainKeys.toArray()));
            } else if (!joiningKeys.contains(joinKey.getKeyInJoining())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in joining exchange! Available keys in main are: " + Arrays.toString(joiningKeys.toArray()));
            }
        }

        // Proceed with join
        List<Map<String, String>> result = join(main, joining);
        this.mainExchange.getIn().setBody(result);
        super.setProcessedRecords(result.size());
        super.postExecute();
        return this.mainExchange;
    }


    public List<Map<String, String>> join(List<Map<String, String>> main, List<Map<String, String>> joining) {
        // Group the collections into maps for easier joining later.
        Map<String, List<Map<String, String>>> mainMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_MAIN, main);
        Map<String, List<Map<String, String>>> joiningMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_JOINING, joining);

        switch (this.joinType) {
            case OUTER:
                return fullJoin(mainMap, joiningMap);
            case INNER:
                return innerJoin(mainMap, joiningMap);
            case LEFT:
                return leftOrRightJoin(mainMap, joiningMap);
            case RIGHT:
                return leftOrRightJoin(joiningMap, mainMap);
        }
        return null;
    }

    /**
     * Joins the 2 exchanges based on inner logic meaning matching keys must exist in
     * both exchanges
     *
     * @param main    the main exchange body
     * @param joining the joining exchange body
     * @return an exchange containing the data found in both collections passed in
     */
    private List<Map<String, String>> innerJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        List<Map<String, String>> result = new ArrayList<>();

        // Iterate main map
        for (String mainKey : main.keySet()) {

            List<Map<String, String>> joinList = joining.get(mainKey);
            if (joinList != null) {

                List<Map<String, String>> mainList = main.get(mainKey);
                for (int i = 0; i < mainList.size(); i++) {
                    Map<String, String> joinMap = joinList.get(i);
                    Map<String, String> mainMap = mainList.get(i);

                    // Inner join requires values to exist in both
                    if (joinMap != null) {

                        // Add the joined map to the result list
                        result.add(JobUtils.getMergedMap(getFields(mainMap, this.entity1Fields), getFields(joinMap, this.entity2Fields)));
                    }
                }
            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> leftOrRightJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        List<Map<String, String>> result = new ArrayList<>();
        Set<String> joiningHeaders = fetchHeader(joining);

        // Iterate main map
        for (String mainKey : main.keySet()) {

            List<Map<String, String>> mainList = main.get(mainKey);

            List<Map<String, String>> joinList = joining.get(mainKey);
            if (joinList == null) {

                // Create dummy list with blank values when joining list is null.
                joinList = new ArrayList<>();
                for(int i = 0; i < main.size(); i++) {
                    joinList.add(createDummyMap(joiningHeaders, this.entity2Fields));
                }

                for (int i = 0; i < mainList.size(); i++) {
                    // Add the joined map to the result list
                    result.add(JobUtils.getMergedMap(getFields(mainList.get(i), this.entity1Fields), getFields(joinList.get(i), this.entity2Fields)));
                }
            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> fullJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        return null;
    }

    private Set<String> fetchHeader(Map<String, List<Map<String, String>>> map) {
        for(Map.Entry<String, List<Map<String, String>>> entry : map.entrySet()) {
            if(entry.getValue() != null && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0).keySet();
            }
        }
        throw new RuntimeException("No map keys could be found in list");
    }

    private Map<String, String> getFields(Map<String, String> mapToTakeFrom, Fields fields) {

        // Add main fields
        if (fields.isAllFields()) {
            return mapToTakeFrom;
        }

        if (fields.hasFields()) {
            Map<String, String> map = new HashMap<>();
            for (Field mainField : fields.getFields()) {
                map.put(mainField.getOutName(), mapToTakeFrom.get(mainField.getName()));
            }
            return map;
        }
        return null;
    }

    private Map<String, String> createDummyMap(Set<String> mapHeaders, Fields fields) {
        Map<String, String> dummyMap = new HashMap<>();

        // Add main fields
        if (fields.isAllFields()) {
            for(String field : mapHeaders) {
                dummyMap.put(field, "");
            }
        }

        if (fields.hasFields()) {
            for (Field mainField : fields.getFields()) {
                dummyMap.put(mainField.getOutName(), "");
            }
        }
        return dummyMap;
    }

    @Override
    public String toString() {
        return "JoinTask{" +
                "mainExchange=" + mainExchange +
                ", joiningExchange=" + joiningExchange +
                ", joinType=" + joinType +
                '}';
    }
}
