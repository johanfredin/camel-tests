package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.ResultType;
import se.fredin.llama.utils.LlamaUtils;

import java.util.*;

/**
 * Used for joining 2 collections similar to how it is made in an sql join
 *
 * @author JFN
 */
public class JoinCollectionsProcessor extends AbstractJoinProcessor {

    private Fields entity1Fields;
    private Fields entity2Fields;

    private List<JoinKey> joinKeys;

    public JoinCollectionsProcessor() {}

    public JoinCollectionsProcessor(Exchange mainExchange, Exchange joiningExchange, List<JoinKey> joinKeys, JoinType joinType, ResultType resultType,
                                    Fields entity1Fields, Fields entity2Fields) {
        super(mainExchange, joiningExchange, joinType, resultType);
        setJoinKeys(joinKeys);
        setEntity1Fields(entity1Fields);
        setEntity2Fields(entity2Fields);
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
    public Exchange doExecuteProcess() {
        var main = LlamaUtils.<Map<String, String>>asList(this.main);
        var joining = LlamaUtils.<Map<String, String>>asList(this.joining);

        // Make sure keys exist
        var mainKeys = main.get(0).keySet();
        var joiningKeys = joining.get(0).keySet();

        for (var joinKey : this.joinKeys) {
            if (!mainKeys.contains(joinKey.getKeyInMain())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in main exchange! Available keys in main are: " + Arrays.toString(mainKeys.toArray()));
            } else if (!joiningKeys.contains(joinKey.getKeyInJoining())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in joining exchange! Available keys in main are: " + Arrays.toString(joiningKeys.toArray()));
            }
        }

        // Proceed with join
        var result = join(main, joining);
        this.main.getIn().setBody(result);
        super.setProcessedRecords(result.size());
        return this.main;
    }


    public List<Map<String, String>> join(List<Map<String, String>> main, List<Map<String, String>> joining) {
        // Group the collections into maps for easier joining later.
        var mainMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_MAIN, main);
        var joiningMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_JOINING, joining);

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
        var result = new ArrayList<Map<String, String>>();

        // Iterate main map
        for (String mainKey : main.keySet()) {

            var joinList = joining.get(mainKey);
            if (joinList != null) {

                var mainList = main.get(mainKey);
                for (int i = 0; i < mainList.size(); i++) {
                    var joinMap = joinList.get(i);
                    var mainMap = mainList.get(i);

                    // Inner join requires values to exist in both
                    if (joinMap != null) {

                        // Add the joined map to the result list
                        result.add(LlamaUtils.getMergedMap(getFields(mainMap, this.entity1Fields), getFields(joinMap, this.entity2Fields)));
                    }
                }
            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> leftOrRightJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        var result = new ArrayList<Map<String, String>>();
        var joiningHeaders = fetchHeader(joining);

        // Iterate main map
        for (var mainKey : main.keySet()) {

            var mainList = main.get(mainKey);
            var joinList = joining.get(mainKey);

            if (joinList == null) {

                // Create dummy list with blank values when joining list is null.
                joinList = new ArrayList<>();
                for(int i = 0; i < main.size(); i++) {
                    joinList.add(createDummyMap(joiningHeaders, this.entity2Fields));
                }

                for (int i = 0; i < mainList.size(); i++) {
                    // Add the joined map to the result list
                    result.add(LlamaUtils.getMergedMap(getFields(mainList.get(i), this.entity1Fields), getFields(joinList.get(i), this.entity2Fields)));
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
        for(var entry : map.entrySet()) {
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
            var map = new HashMap<String, String>();
            for (var mainField : fields.getFields()) {
                map.put(mainField.getOutName(), mapToTakeFrom.get(mainField.getName()));
            }
            return map;
        }
        return null;
    }

    private Map<String, String> createDummyMap(Set<String> mapHeaders, Fields fields) {
        var dummyMap = new HashMap<String, String>();

        // Add main fields
        if (fields.isAllFields()) {
            for(var field : mapHeaders) {
                dummyMap.put(field, "");
            }
        }

        if (fields.hasFields()) {
            for (var mainField : fields.getFields()) {
                dummyMap.put(mainField.getOutName(), "");
            }
        }
        return dummyMap;
    }

    @Override
    public String toString() {
        return "JoinCollectionsProcessor{" +
                "entity1Fields=" + entity1Fields +
                ", entity2Fields=" + entity2Fields +
                ", joinKeys=" + joinKeys +
                ", main=" + main +
                ", joining=" + joining +
                ", joinType=" + joinType +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
