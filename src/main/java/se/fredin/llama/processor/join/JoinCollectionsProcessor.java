package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.Fields;
import se.fredin.llama.processor.Keys;
import se.fredin.llama.processor.ResultType;
import se.fredin.llama.utils.LlamaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Used for joining 2 collections similar to how it is made in an sql filterValidateAgainst
 *
 * @author JFN
 */
public class JoinCollectionsProcessor extends AbstractJoinProcessor {

    private Fields entity1Fields;
    private Fields entity2Fields;
    private Keys joinKeys;

    public JoinCollectionsProcessor() {}

    public JoinCollectionsProcessor(Keys joinKeys, JoinType joinType, ResultType resultType, Fields entity1Fields, Fields entity2Fields) {
        setJoinType(joinType);
        setResultType(resultType);
        setJoinKeys(joinKeys);
        setEntity1Fields(entity1Fields);
        setEntity2Fields(entity2Fields);
    }

    public JoinCollectionsProcessor(Exchange mainExchange, Exchange joiningExchange, Keys joinKeys, JoinType joinType, ResultType resultType,
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

    public Keys getJoinKeys() {
        return joinKeys;
    }

    public void setJoinKeys(Keys joinKeys) {
        this.joinKeys = joinKeys;
    }

    @Override
    public Exchange doExecuteProcess() {
        var main = LlamaUtils.<Map<String, String>>asList(this.main);
        var joining = LlamaUtils.<Map<String, String>>asList(this.joining);

        // Make sure keys exist
        var mainKeys = main.get(0).keySet();
        var joiningKeys = joining.get(0).keySet();

        for (var joinKey : this.joinKeys.getKeys()) {
            if (!mainKeys.contains(joinKey.getKeyInMain())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in main exchange! Available keys in main are: " + Arrays.toString(mainKeys.toArray()));
            } else if (!joiningKeys.contains(joinKey.getKeyInJoining())) {
                throw new RuntimeException("Join key=" + joinKey.getKeyInMain() + " does not exist in joining exchange! Available keys in main are: " + Arrays.toString(joiningKeys.toArray()));
            }
        }

        // Proceed with filterValidateAgainst
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
            case LEFT_EXCLUDING:
                return leftOrRightExcludingJoin(mainMap, joiningMap);
            case RIGHT_EXCLUDING:
                return leftOrRightExcludingJoin(joiningMap, mainMap);
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

            // Fetch the matching group in the joining map (if any)
            var joinList = joining.get(mainKey);
            if (joinList != null) {

                // Fetch the list connected to the current id in the main map
                var mainList = main.get(mainKey);
                for (var mainMap : mainList) {

                    /*
                     * Now iterate the group of maps in the joining list
                     * and enrich the main map with those entries.
                     */
                    for(var joinMap : joinList) {

                        // Inner filterValidateAgainst requires values to exist in both
                        if (joinMap != null) {

                            // Merge the 2 maps and add it to the result list.
                            var recordsMain = JoinUtils.getFields(mainMap, this.entity1Fields);
                            var recordsJoining = JoinUtils.getFields(joinMap, this.entity2Fields);
                            result.add(JoinUtils.createMergedMap(recordsMain, recordsJoining));
                        }
                    }
                }
            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> leftOrRightJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        var result = new ArrayList<Map<String, String>>();
        var joiningHeaders = JoinUtils.fetchHeader(joining);

        // Determine what to pass in as main and joining field headers.
        var mainFields = this.joinType == JoinType.LEFT ? this.entity1Fields : this.entity2Fields;
        var joiningFields = this.joinType == JoinType.LEFT ? this.entity2Fields : this.entity1Fields;

        // Iterate main map
        for (var mainKey : main.keySet()) {

            var mainList = main.get(mainKey);
            var joinList = joining.get(mainKey);

            if (joinList == null) {

                // Create dummy list with blank values when joining list is null.
                joinList = new ArrayList<>();
                for(int i = 0; i < mainList.size(); i++) {
                    joinList.add(JoinUtils.createDummyMap(joiningHeaders, joiningFields));
                }
            }

            for (var mainMap : mainList) {
                // Add the joined map to the result list
                for(var joinMap : joinList) {
                    var recordsMain = JoinUtils.getFields(mainMap, mainFields);
                    var recordsJoining = JoinUtils.getFields(joinMap, joiningFields);
                    result.add(JoinUtils.createMergedMap(recordsMain, recordsJoining));
                }
            }
        }

        // Add to the exchange
        return result;
    }

    /**
     * Used with Join types {@link JoinType#LEFT_EXCLUDING}, {@link JoinType#RIGHT_EXCLUDING} meaning
     * only records that exist in one of the two exchanges will be kept in the result. Only records
     * from the main map passed in are kept regardless of whether we told the processor to include fields
     * in the joining map. Then again that would be pointless since they would always be null
     * @param main the map containing the data we need to verify is unique.
     * @param joining the joining map to check against the main map.
     * @return a collection of maps containing records that only exists in the main map.
     */
    private List<Map<String, String>> leftOrRightExcludingJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        // Determine what to pass in as main and joining field headers.
        var mainFields = this.joinType == JoinType.LEFT_EXCLUDING ? this.entity1Fields : this.entity2Fields;
        var joiningFields = this.joinType == JoinType.LEFT_EXCLUDING ? this.entity2Fields : this.entity1Fields;

        var result = new ArrayList<Map<String, String>>();

        // Iterate main map
        for (var mainKey : main.keySet()) {

            var mainList = main.get(mainKey);

            if (joining.get(mainKey) == null) {

                for (var mainMap : mainList) {
                    result.add(JoinUtils.getFields(mainMap, mainFields));
                }
            }
        }

        // Add to the exchange
        return result;
    }

    private List<Map<String, String>> fullJoin(Map<String, List<Map<String, String>>> main, Map<String, List<Map<String, String>>> joining) {
        return null;
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
