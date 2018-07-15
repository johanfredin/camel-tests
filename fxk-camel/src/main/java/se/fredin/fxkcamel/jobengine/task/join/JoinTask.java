package se.fredin.fxkcamel.jobengine.task.join;

import org.apache.camel.Exchange;
import se.fredin.fxkcamel.jobengine.task.BaseTask;
import se.fredin.fxkcamel.jobengine.utils.JobUtils;

import java.util.*;

/**
 * Used for joining 2 collections similar to how it was made in the fxk connector
 *
 * @author JFN
 */
public class JoinTask extends BaseTask {

    public final byte EXCHANGE_MAIN = 0;
    public final byte EXCHANGE_JOINING = 1;

    private Exchange mainExchange;
    private Exchange joiningExchange;
    private JoinType joinType;

    private String[] entity1Fields;
    private String[] entity2Fields;

    private List<JoinKey> joinKeys;

    public JoinTask() {
        super("Join Task");
    }

    public JoinTask(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> joinKeys, JoinType joinType, String[] entity1Fields, String[] entity2Fields) {
        super("Join Task");
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

    public String[] getEntity1Fields() {
        return entity1Fields;
    }

    public void setEntity1Fields(String... entity1Fields) {
        this.entity1Fields = entity1Fields;
    }

    public String[] getEntity2Fields() {
        return entity2Fields;
    }

    public void setEntity2Fields(String... entity2Fields) {
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
        Map<String, List<Map<String, String>>> mainMap = groupCollection(EXCHANGE_MAIN, main);
        Map<String, List<Map<String, String>>> joiningMap = groupCollection(EXCHANGE_JOINING, joining);

        switch (this.joinType) {
            case FULL:
                return fullJoin(main, joining);
            case INNER:
                return innerJoin(mainMap, joiningMap);
            case LEFT:
                return leftJoin(main, joining);
            case RIGHT:
                return rightJoin(main, joining);
        }
        return null;
    }

    private Map<String, List<Map<String, String>>> groupCollection(byte exchange, List<Map<String, String>> list) {
        Map<String, List<Map<String, String>>> map = new HashMap<>();
        for (Map<String, String> m : list) {

            // First set the key
            String key = "";
            for (JoinKey joinKey : this.joinKeys) {
                key += m.get(exchange == EXCHANGE_MAIN ? joinKey.getKeyInMain() : joinKey.getKeyInJoining());
            }

            // Now group
            List<Map<String, String>> value = map.get(key);
            if (value == null) {
                value = new ArrayList<>();
            }

            // Add new entry
            value.add(m);

            // Update map
            map.put(key, value);
        }

        return map;
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

                        Map<String, String> resultMap = new HashMap<>();

                        // Add main fields
                        for (String mainField : this.entity1Fields) {
                            resultMap.put(mainField, mainMap.get(mainField));
                        }

                        // Add join fields
                        for (String joinField : this.entity2Fields) {
                            resultMap.put(joinField, joinMap.get(joinField));
                        }

                        // Add the joined map to the result list
                        result.add(resultMap);
                    }
                }

            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> leftJoin(List<Map<String, String>> main, List<Map<String, String>> joining) {
        return null;
    }

    private List<Map<String, String>> rightJoin(List<Map<String, String>> main, List<Map<String, String>> joining) {
        return null;
    }

    private List<Map<String, String>> fullJoin(List<Map<String, String>> main, List<Map<String, String>> joining) {
        return null;
    }

    private String keysAsString(byte exchange) {
        String key = "";
        for (JoinKey joinKey : this.joinKeys) {
            key += exchange == EXCHANGE_MAIN ? joinKey.getKeyInMain() : joinKey.getKeyInJoining();
        }
        return key;
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
