/*
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

import com.github.johanfredin.llama.collection.LlamaList;
import com.github.johanfredin.llama.collection.LlamaMap;
import com.github.johanfredin.llama.processor.AbstractJoinProcessor;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This processor simply merges the content of 2 collections into one.
 * This would be useful when we have similar collections that we just want to squash into one.
 * Unlike the {@link JoinCollectionsProcessor} the MergeCollectionsProcessor will not match any
 * records. Merging here simply means first add everything in the main exchange and then everything in the
 * merging exchange and return that in one combined collection. Like so:
 * <ul>
 *     <li>Header fields (key-set) are combined into one</li>
 *     <li>Content of the first exchange is added</li>
 *     <li>Content of the second exchange is added</li>
 *     <li>Values that does not exist in the other exchange come out as empty strings</li>
 * </ul>
 * <br>
 * <b>Example:</b><br>
 * <p>
 * <u>Exchange 1</u><br>
 * Id;Name;Age<br>
 * 1;Joe;25
 * <p>
 *  <u>Exchange 1</u><br>
 *  Id;Profession;Email<br>
 *  2;Programmer;info@mail.com
 *  
 *  <p>
 *  <u>Result</u><br>
 *  Id;Name;Age;Profession;Email<br>
 *  1;Joe;25;;<br>
 *  2;;;Programmer;info@mail.com
 *  <br>
 *  <br>
 * As with every other {@link GenericProcessor} the body of the exchanges are expected to
 * be a List of Maps
 *
 * @author johan fredin
 */
public class MergeCollectionsProcessor extends AbstractJoinProcessor {

    /**
     * Create a new empty instance. Package private, used for
     * unit testing only.
     */
    MergeCollectionsProcessor() {}

    /**
     * Create a new instance
     * @param main the main exchange
     * @param joining the exchange we want to merge with the main exchange.
     * @param includeHeader whether or not to include the header (default is false)
     */
    public MergeCollectionsProcessor(Exchange main, Exchange joining, boolean includeHeader) {
        super(main, joining, null);
        setIncludeHeader(includeHeader);
    }

    @Override
    public void process() {
        var mainList = LlamaUtils.asListOfMaps(getMain());
        var mergeList = LlamaUtils.asListOfMaps(getJoining());

        var result = merge(mainList, mergeList);

        setProcessedRecords(result.size());

        if (isIncludeHeader()) {
            result.add(0, getHeader(result.get(0).keySet()));
        }

        getMain().getIn().setBody(result);
    }

    /**
     * Combines the headers (key-sets) into one collection and then
     * adds both the content of the main and the merging exchange into one collection.
     * @param mainList the body of the main exchange
     * @param mergeList the body of the merging exchange
     * @return the content of the main and the merging exchange in one collection.
     */
    List<Map<String, String>> merge(List<Map<String, String>> mainList, List<Map<String, String>> mergeList) {
        var keys = LlamaList.of(mainList.get(0).keySet(), mergeList.get(0).keySet());
        return LlamaList.of(getContent(mainList, keys), getContent(mergeList, keys));
    }

    private List<Map<String, String>> getContent(List<Map<String, String>> content, final Collection<String> keys) {
        var result = new LlamaList<Map<String, String>>();
        content.forEach(record -> {
            var newRecord = new LlamaMap<String, String>();
            for (String key : keys) {
                newRecord.put(key, record.get(key) == null ? "" : record.get(key));
            }
            result.add(newRecord);
        });
        return result;
    }

    @Override
    public String toString() {
        return "MergeCollectionsProcessor{" +
                "main=" + main +
                ", joining=" + joining +
                ", includeHeader=" + includeHeader +
                ", initialRecords=" + initialRecords +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
