/**
 * Copyright 2018 Johan Fredin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.johanfredin.llama.processor.generic;

import com.github.johanfredin.llama.pojo.Fields;
import com.github.johanfredin.llama.pojo.JoinType;
import com.github.johanfredin.llama.pojo.Keys;
import com.github.johanfredin.llama.processor.AbstractJoinProcessor;
import com.github.johanfredin.llama.utils.JoinUtils;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.*;

/**
 * Used for joining 2 collections similar to how it is made in an sql.<br>
 * Takes in two {@link Exchange} objects whose bodies are expected to contain<br>
 * a list och maps with key/value={@link String}. We can specify what {@link com.github.johanfredin.llama.pojo.Field}(s)<br>
 * in those maps to use as {@link com.github.johanfredin.llama.pojo.JoinKey}(s). We can also decide what fields we want and<br>
 * (optionally) give them new output names. By default {@link Fields#ALL} will be used<br>
 * and join type will be {@link JoinType#INNER}.<br>
 * <br>
 * When there are fields with the same name in <br>
 * both collections and we have not given any of those unique output names. Then the<br>
 * owning exchanges ({@link #main} when join type one of {@link JoinType#LEFT}, {@link JoinType#RIGHT}, {@link #joining} when<br>
 * {@link JoinType#RIGHT}) field will rule.<br>
 * <br>
 * When using {@link JoinType#RIGHT_EXCLUDING}, {@link JoinType#LEFT_EXCLUDING}, only one exchanges fields will exist<br>
 * in the result (main when left_excluding, joining when right_excluding).<br>
 * <br>
 * The result of the join will be placed in the {@link #main} exchange and returned back to the route that called this processor.
 *
 * @author johan
 */
public class JoinCollectionsProcessor extends AbstractJoinProcessor {

    private Fields entity1Fields;
    private Fields entity2Fields;
    private Keys joinKeys;

    /**
     * Create a new instance. This constructor will
     * not assign the exchanges and should be used with unit tests only.
     * Hence why it is marked protected
     *
     * @param joinKeys      the fields used as keys.
     * @param joinType      how to join the exchanges.
     * @param entity1Fields the fields we want from the main exchange
     * @param entity2Fields the fields we want from the joining exchange
     */
    protected JoinCollectionsProcessor(Keys joinKeys, JoinType joinType, Fields entity1Fields, Fields entity2Fields) {
        this(joinKeys, joinType, entity1Fields, entity2Fields, false);
    }

    /**
     * Create a new instance. This constructor will
     * not assign the exchanges and should be used with unit tests only.
     * Hence why it is marked protected
     *
     * @param joinKeys      the fields used as keys.
     * @param joinType      how to join the exchanges.
     * @param entity1Fields the fields we want from the main exchange
     * @param entity2Fields the fields we want from the joining exchange
     * @param includeHeader whether or not to include the header as the first entry in the result collection (default is false)
     */
    protected JoinCollectionsProcessor(Keys joinKeys, JoinType joinType, Fields entity1Fields, Fields entity2Fields, boolean includeHeader) {
        setJoinType(joinType);
        setJoinKeys(joinKeys);
        setEntity1Fields(entity1Fields);
        setEntity2Fields(entity2Fields);
        setIncludeHeader(includeHeader);
    }

    /**
     * Create a new instance
     *
     * @param mainExchange    the main exchange
     * @param joiningExchange the joining exchange
     * @param joinKeys        the fields used as keys.
     * @param joinType        how to join the exchanges.
     * @param entity1Fields   the fields we want from the main exchange
     * @param entity2Fields   the fields we want from the joining exchange
     * @param includeHeader   whether or not to include the header as the first entry in the result collection (default is false)
     */
    public JoinCollectionsProcessor(Exchange mainExchange, Exchange joiningExchange, Keys joinKeys, JoinType joinType,
                                    Fields entity1Fields, Fields entity2Fields, boolean includeHeader) {
        super(mainExchange, joiningExchange, joinType);
        setJoinKeys(joinKeys);
        setEntity1Fields(entity1Fields);
        setEntity2Fields(entity2Fields);
        setIncludeHeader(includeHeader);
    }

    /**
     * @return the join type used
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * @param joinType the join type to use.
     */
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    /**
     * The fields we want from the main exchange. When JoinType={@link JoinType#RIGHT_EXCLUDING} then
     * these fields will never be included in the result regardless of whether they were populated or not.
     *
     * @return the fields we want from the main exchange
     */
    public Fields getEntity1Fields() {
        return entity1Fields;
    }

    /**
     * Set the fields we want from the main exchange. When JoinType={@link JoinType#RIGHT_EXCLUDING} then
     * these fields will never be included in the result regardless of whether they were populated or not.
     *
     * @param entity1Fields the fields we want from the main exchange
     */
    public void setEntity1Fields(Fields entity1Fields) {
        this.entity1Fields = entity1Fields;
    }

    /**
     * The fields we want from the joining exchange. When JoinType={@link JoinType#LEFT_EXCLUDING} then
     * these fields will never be included in the result regardless of whether they were populated or not.
     *
     * @return the fields we want from the joining exchange
     */
    public Fields getEntity2Fields() {
        return entity2Fields;
    }

    /**
     * Set the fields we want from the joining exchange. When JoinType={@link JoinType#LEFT_EXCLUDING} then
     * these fields will never be included in the result regardless of whether they were populated or not.
     *
     * @param entity2Fields the fields we want from the joining exchange
     */
    public void setEntity2Fields(Fields entity2Fields) {
        this.entity2Fields = entity2Fields;
    }

    /**
     * @return the fields used as join keys in the exchanges
     */
    public Keys getJoinKeys() {
        return joinKeys;
    }

    /**
     * @param joinKeys the fields used as join keys in the exchanges
     */
    public void setJoinKeys(Keys joinKeys) {
        this.joinKeys = joinKeys;
    }

    @Override
    public void process() {
        var main = LlamaUtils.<Map<String, String>>asListOfMaps(this.main);
        var joining = LlamaUtils.<Map<String, String>>asListOfMaps(this.joining);

        super.setInitialRecords(main.size());

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

        // Proceed with join and give the result to a linked list
        var result = new LinkedList<>(join(main, joining));

        // Give the header to the list if specified
        if (super.includeHeader) {
            result.add(0, super.getHeader(result.get(0).keySet()));
        }

        // Update the main exchange
        this.main.getIn().setBody(result);
        super.setProcessedRecords(result.size());
    }

    /**
     * Once the exchanges bodies have been fetched and verified as collections of maps then this method gets called.
     * Switches on the join type and sends the collections further for nested join methods depending on type.
     *
     * @param main    the body of the main exchange
     * @param joining the body of the joining exchange
     * @return the collection holding the result of the join. Or null if no join type given.
     */
    protected List<Map<String, String>> join(List<Map<String, String>> main, List<Map<String, String>> joining) {
        // Group the collections into maps for easier joining later.
        var mainMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_MAIN, main);
        var joiningMap = JoinUtils.groupCollection(this.joinKeys, JoinUtils.EXCHANGE_JOINING, joining);

        switch (this.joinType) {
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
                    for (var joinMap : joinList) {

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

    /**
     * Joins the 2 exchanges based on left/right logic, meaning that a result will always be added from joining collection but
     * filled with empty values when no match exists. To make a right join, call this method and simply reverse the parameters.
     *
     * @param main    the body of the main exchange
     * @param joining the body of the joining exchange.
     * @return the result of the join
     */
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
                for (int i = 0; i < mainList.size(); i++) {
                    joinList.add(JoinUtils.createDummyMap(joiningHeaders, joiningFields));
                }
            }

            for (var mainMap : mainList) {
                // Add the joined map to the result list
                for (var joinMap : joinList) {
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
     * in the joining map or not. Then again that would be pointless since they would always be null
     *
     * @param main    the map containing the data we need to verify is unique.
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

    @Override
    public String toString() {
        return "JoinCollectionsProcessor{" +
                "entity1Fields=" + entity1Fields +
                ", entity2Fields=" + entity2Fields +
                ", joinKeys=" + joinKeys +
                ", main=" + main +
                ", joining=" + joining +
                ", joinType=" + joinType +
                ", includeHeader=" + includeHeader +
                ", initialRecords=" + initialRecords +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
