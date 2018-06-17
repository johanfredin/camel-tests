package se.fredin.fxkcamel.jobengine.bean;

import java.io.Serializable;

public interface JobEngineBean<ID extends Serializable> extends Serializable {

    ID getId();

}
