package smash.teams.be.model.schedule;

public enum Status {
    APPROVED("APPROVED"), REJECTED("REJECTED"), WAITING("WAITING");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
