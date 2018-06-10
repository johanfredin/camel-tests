public class Main {

    public static void main(String[] args) {
        Person julia = new Person("Julia", 4, Gender.GIRL, "glad");
        Person pappa = new Person("Johan", 32, Gender.BOY, "glad");

        System.out.println(julia.sayHello());
        System.out.println(pappa.sayHello());
    }

    public static class Person {

        String name;
        int age;
        Gender gender;
        String mood;

        public Person(String name, int age, Gender gender, String mood) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.mood = mood;
        }

        String sayHello() {
            return name + " är en " + mood + " " + gender.getLabel() + " som är " + age + " år gammal";
        }

    }

    enum Gender {
        BOY("pojke"),
        GIRL("flicka");

        private String label;

        Gender(String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
