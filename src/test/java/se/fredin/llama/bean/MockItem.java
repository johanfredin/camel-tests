package se.fredin.llama.bean;

import se.fredin.llama.jobengine.bean.FxKBean;

public class MockItem implements FxKBean {

    private String itemNo;
    private String name;

    public MockItem(String id) {
        this(id, null);
    }

    public MockItem(String itemNo, String name) {
        this.itemNo = itemNo;
        this.name = name;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return this.itemNo;
    }
}
