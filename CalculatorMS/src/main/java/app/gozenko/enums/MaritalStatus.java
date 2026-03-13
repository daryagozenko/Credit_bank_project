package app.gozenko.enums;

public enum MaritalStatus {
    MARRIED("В браке"),
    NOT_MARRIED("Не состоит в браке"),
    DIVORCED("В разводе");

    private final String title;

    MaritalStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
