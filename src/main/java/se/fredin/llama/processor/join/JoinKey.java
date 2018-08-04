package se.fredin.llama.processor.join;

/**
 * Used in {@link JoinProcessor}. Holds 2 fields representing key in main
 * and key in joining where their values must match.
 */
public class JoinKey {

    private String keyInMain;
    private String keyInJoining;

    public JoinKey(String keyInMain, String keyInJoining) {
        this.keyInMain = keyInMain;
        this.keyInJoining = keyInJoining;
    }

    public String getKeyInMain() {
        return keyInMain;
    }

    public void setKeyInMain(String keyInMain) {
        this.keyInMain = keyInMain;
    }

    public String getKeyInJoining() {
        return keyInJoining;
    }

    public void setKeyInJoining(String keyInJoining) {
        this.keyInJoining = keyInJoining;
    }

    @Override
    public String toString() {
        return "JoinKey{" +
                "keyInMain='" + keyInMain + '\'' +
                ", keyInJoining='" + keyInJoining + '\'' +
                '}';
    }


}
