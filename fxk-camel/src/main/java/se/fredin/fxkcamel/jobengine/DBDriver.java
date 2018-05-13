package se.fredin.fxkcamel.jobengine;

public enum DBDriver {

    MYSQL("com.mysql"),
    DB2("com.ibm.db2");

    private String name;

    DBDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
