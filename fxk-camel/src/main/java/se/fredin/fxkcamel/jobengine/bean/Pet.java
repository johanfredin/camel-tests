package se.fredin.fxkcamel.jobengine.bean;

public class Pet {

    private long id;
    private String name;
    private String gender;
    private String type;

    public Pet() {}

    public Pet(int id, String name, String gender, String type) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
