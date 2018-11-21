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
package com.github.johanfredin.llama.processor.bean;

import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.processor.BaseProcessor;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.ArrayList;
import java.util.List;

public class UnionBeansProcessor extends BaseProcessor {

    private Exchange mergingExchange;
    private Exchange mainExchange;

    private Exchange resultingExchange;

    public UnionBeansProcessor(Exchange mergingExchange, Exchange mainExchange) {
        this.setMergingExchange(mergingExchange);
        this.setMainExchange(mainExchange);
    }

    public Exchange getMergingExchange() {
        return mergingExchange;
    }

    public void setMergingExchange(Exchange mergingExchange) {
        this.mergingExchange = mergingExchange;
    }

    public Exchange getMainExchange() {
        return mainExchange;
    }

    public void setMainExchange(Exchange mainExchange) {
        this.mainExchange = mainExchange;
    }

    @Override
    public void process() {
        var newBean = getMergingExchange().getIn().getBody(LlamaBean.class);
        List<LlamaBean> beans;
        if (this.mainExchange == null) {
            beans = new ArrayList<>();

            if (newBean != null) {
                beans.add(newBean);
            }

            this.mergingExchange.getIn().setBody(beans);
            this.resultingExchange = this.mergingExchange;
        }

        beans = LlamaUtils.asLlamaBeanList(this.mainExchange);
        if (newBean != null) {
            beans.add(newBean);
        }
        super.incProcessedRecords();
        this.mainExchange.getIn().setBody(beans);
        this.resultingExchange = this.mainExchange;
    }

    @Override
    public Exchange getResult() {
        return this.resultingExchange;
    }

    @Override
    public String toString() {
        return "UnionBeansProcessor{" +
                "mergingExchange=" + mergingExchange +
                ", mainExchange=" + mainExchange +
                '}';
    }
}
