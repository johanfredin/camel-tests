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
package com.github.johanfredin.llama.bean;

/**
 * A mock llama bean implementation
 */
public class MockItemAsset implements LlamaBean {

    private String itemNo;
    private String quality;
    private String uncPath;

    public MockItemAsset(String id) {
        this(id, null, null);
    }

    public MockItemAsset(String itemNo, String quality, String uncPath) {
        this.itemNo = itemNo;
        this.quality = quality;
        this.uncPath = uncPath;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getUncPath() {
        return uncPath;
    }

    public void setUncPath(String uncPath) {
        this.uncPath = uncPath;
    }

    @Override
    public String getId() {
        return this.itemNo;
    }
}
