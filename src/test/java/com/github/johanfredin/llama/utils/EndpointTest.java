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
package com.github.johanfredin.llama.utils;

import org.junit.Test;
import com.github.johanfredin.llama.bean.LlamaBean;

import static org.junit.Assert.assertEquals;

/**
 * Test for the {@link Endpoint} class
 */
public class EndpointTest {

    private static final String DIR = "/some/directory";
    private static final String FILE_NAME = "fileName.file";
    private static final String SQL = "select * from my_table";

    @Test
    public void testFile2Params() {
        String fileUrl = Endpoint.file(DIR, FILE_NAME);
        assertEquals("file:" + DIR + "/?fileName=" + FILE_NAME + "&noop=true", fileUrl);
    }

    @Test
    public void testFile3Params() {
        String fileUrl = Endpoint.file(DIR, FILE_NAME, InputOptions.DISCARD);
        assertEquals("file:" + DIR + "/?fileName=" + FILE_NAME + "&noop=false", fileUrl);
    }

    @Test
    public void testFileSource() {
        String fileUrl = Endpoint.fileSource(InputType.JPA, DIR, FILE_NAME, InputOptions.KEEP);
        assertEquals("jpa:" + DIR + "/?fileName=" + FILE_NAME + "&noop=true", fileUrl);
    }

    @Test
    public void testSql2Params() {
        String sqlUri = Endpoint.sql(SQL, LlamaBean.class);
        assertEquals("sql:" + SQL + "?outputClass=com.github.johanfredin.llama.bean.LlamaBean&noop=true&useIterator=false", sqlUri);
    }

    @Test
    public void testSql4Params() {
        String sqlUri = Endpoint.sql(SQL, LlamaBean.class, false, SqlResultType.ITERATE);
        assertEquals("sql:" + SQL + "?outputClass=com.github.johanfredin.llama.bean.LlamaBean&noop=false&useIterator=true", sqlUri);
    }

    @Test
    public void testSql5Params() {
        String sqlUri = Endpoint.sql(SQL, LlamaBean.class, true, SqlResultType.ITERATE, "myDataSource");
        assertEquals("sql:" + SQL + "?outputClass=com.github.johanfredin.llama.bean.LlamaBean&noop=true&useIterator=true&datasource=#myDataSource", sqlUri);
    }

}