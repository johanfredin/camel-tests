package se.fredin.fxkcamel.jobengine.mock.bean;

import java.io.Serializable;

public class MockItem implements JobEngineBean {

    private String itemNo;
    private String name;

    public MockItem() {}

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
    public Serializable getId() {
        return getItemNo();
    }
}
