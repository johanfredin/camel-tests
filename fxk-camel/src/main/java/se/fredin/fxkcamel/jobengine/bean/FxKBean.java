package se.fredin.fxkcamel.jobengine.bean;

import java.io.Serializable;

public abstract class FxKBean<ID extends Serializable> implements Serializable {

    public abstract ID getId();

}
