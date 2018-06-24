package se.fredin.fxkcamel.jobengine.bean;

import java.io.Serializable;

public abstract class FxKBean<ID extends Serializable> implements Serializable {

    protected ID id;

    public FxKBean(ID id) {
        setId(id);
    }

    public ID getId() {
        return this.id;
    }

    public abstract void setId(ID id);
}
