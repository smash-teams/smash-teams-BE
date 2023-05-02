package smash.teams.be.model.schedule;

public enum Type {
    DAYOFF("연차"), HALFOFF("반차"), SHIFT("당직");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
