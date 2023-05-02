package smash.teams.be.model.user;

public enum Role {
    ADMIN("ADMIN"), CEO("CEO"), MANAGER("MANAGER"), USER("USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
