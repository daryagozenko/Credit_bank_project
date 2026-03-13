package app.gozenko.enums;

public enum EmploymentStatus {
    WORK("Трудоустроен"),
    NOT_WORK("Не трудоустроен");

    private final String title;

    EmploymentStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
