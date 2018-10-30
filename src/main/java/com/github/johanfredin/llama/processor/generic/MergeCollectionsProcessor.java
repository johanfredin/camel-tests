package com.github.johanfredin.llama.processor.generic;

import com.github.johanfredin.llama.pojo.LlamaMap;
import com.github.johanfredin.llama.processor.AbstractJoinProcessor;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.*;
import java.util.stream.Collectors;


public class MergeCollectionsProcessor extends AbstractJoinProcessor {

    public MergeCollectionsProcessor(Exchange main, Exchange joining) {
        this(main, joining, false);
    }

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
        this.main.getIn().setBody(result);
    }

    protected LinkedList<Map<String, String>> merge(List<Map<String, String>> mainList, List<Map<String, String>> mergeList) {
        var keys = getAllHeaderFields(mainList.get(0).keySet(), mergeList.get(0).keySet());
        var result = new LinkedList<Map<String, String>>();

        result.addAll(getContent(mainList, keys));
        result.addAll(getContent(mergeList, keys));

        if (this.includeHeader) {
            result.add(0, getHeader(keys));
        }

        return result;
    }

    protected List<Map<String, String>> getContent(List<Map<String, String>> content, final Collection<String> keys) {
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

    protected Collection<String> getAllHeaderFields(Set<String> mainKeys, Set<String> mergeKeys) {
        mainKeys.addAll(mergeKeys);
        return mainKeys;

    }
}
