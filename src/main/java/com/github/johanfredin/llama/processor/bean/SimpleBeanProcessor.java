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
package com.github.johanfredin.llama.processor.bean;

import com.github.johanfredin.llama.bean.LlamaBean;
import com.github.johanfredin.llama.processor.BaseProcessor;
import com.github.johanfredin.llama.utils.LlamaUtils;
import org.apache.camel.Exchange;

import java.util.List;

/**
 * Superclass for "simple" processors that modifies a singe exchange.
 * This could very easily be achieved by calling .stream() on a collection
 * and SHOULD be the way to do it if we have several things we need to do (transformBeans, sort, filter etc).
 * However if we know we simply want to do one thing to our collection and want to
 * have the code a bit less bloated then this could be a bit more elegant.
 * Body of the exchange is expected to contain a {@link List} of type {@link LlamaBean}.
 *
 * @param <T> any class extending {@link LlamaBean}
 */
public abstract class SimpleBeanProcessor<T extends LlamaBean> extends BaseProcessor {

    protected Exchange exchange;

    /**
     * Create a new instance calling super first.
     *
     * @param exchange the exchange to process.
     */
    public SimpleBeanProcessor(Exchange exchange) {
        setExchange(exchange);
    }

    /**
     * @param exchange the exchange to process
     */
    protected void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the exchange to process
     */
    public Exchange getExchange() {
        return exchange;
    }

    @Override
    public Exchange getResult() {
        return this.exchange;
    }

    @Override
    public void process() {
        var beans = LlamaUtils.<T>asLlamaBeanList(this.exchange);
        this.exchange.getIn().setBody(processData(beans));
    }

    /**
     * The result of this method will be what we give the exchange.
     * What we do with the passed in collection is decided in the subclasses that
     * will be forced to implement this method.
     *
     * @param beans the beans to modify
     * @return the passed in beans, modified.
     */
    public abstract List<T> processData(List<T> beans);

}
