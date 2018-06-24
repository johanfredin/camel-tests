package se.fredin.fxkcamel.jobengine.bean;

public class MockItem extends FxKBean<String> {

    private String itemNo;
    private String name;

    public MockItem(String id) {
        this(id, null);
    }

    public MockItem(String itemNo, String name) {
        super(itemNo);
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
    public void setId(String s) {
        this.itemNo = s;
    }
}
