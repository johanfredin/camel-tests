package com.github.johanfredin.llama.processor.generic;

import com.github.johanfredin.llama.collection.LlamaList;
import com.github.johanfredin.llama.collection.LlamaMap;
import com.github.johanfredin.llama.processor.AbstractJoinProcessor;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simplified version of the {@link JoinCollectionsProcessor}. This processor simply
 * merges the content of 2 collections into one. Merging happens the following way:
 * <ul>
 *     <li>Header fields (key-set) are combined into one</li>
 *     <li>Content of the first exchange is added</li>
 *     <li>Content of the second exchange is added</li>
 *     <li>Values that does not exist in the other exchange come out as empty strings</li>
 * </ul>
 * <br/>
 * <b>Example:</b><br>
 * <p/>
 * <u>Exchange 1</u><br/>
 * Id;Name;Age<br/>
 * 1;Joe;25
 * <p/>
 *  <u>Exchange 1</u><br/>
 *  Id;Profession;Email<br/>
 *  2;Programmer;info@mail.com
 *  
 *  <p/>
 *  <u>Result</u><br>
 *  Id;Name;Age;Profession;Email<br/>
 *  1;Joe;25;;<br/>
 *  2;;;Programmer;info@mail.com
 *  <br/>
 *
 * @author johan fredin
 */
public class MergeCollectionsProcessor extends AbstractJoinProcessor {

    MergeCollectionsProcessor() {}

    public MergeCollectionsProcessor(Exchange main, Exchange joining, boolean includeHeader) {
        super(main, joining, null);
        this.includeHeader = includeHeader;
    }

    @Override
    public void process() {
        var mainList = LlamaUtils.asListOfMaps(this.main);
        var mergeList = LlamaUtils.asListOfMaps(this.joining);

        var result = merge(mainList, mergeList);

        this.processedRecords = result.size();

        if (this.includeHeader) {
            result.add(0, getHeader(result.get(0).keySet()));
        }

        this.main.getIn().setBody(result);
    }

    List<Map<String, String>> merge(List<Map<String, String>> mainList, List<Map<String, String>> mergeList) {
        var keys = LlamaList.of(mainList.get(0).keySet(), mergeList.get(0).keySet());
        return LlamaList.of(getContent(mainList, keys), getContent(mergeList, keys));
    }

    private List<Map<String, String>> getContent(List<Map<String, String>> content, final Collection<String> keys) {
        var result = new LinkedList<Map<String, String>>();
        content.forEach(record -> {
            var newRecord = keys
                    .stream()
                    .collect(
                            Collectors.toMap(key -> key, key -> record.get(key) == null ? "" :
                                    record.get(key), (a, b) -> b, LlamaMap::new));
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
