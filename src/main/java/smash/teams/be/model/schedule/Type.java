package smash.teams.be.model.schedule;

public enum Type {
    DAYOFF("DAYOFF"), HALFOFF("HALFOFF"), SHIFT("SHIFT");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
