package smash.teams.be.model.schedule;

public enum Status {
    APPROVED("APPROVED"), REJECTED("REJECTED"), FIRST("FIRST"), LAST("LAST");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
