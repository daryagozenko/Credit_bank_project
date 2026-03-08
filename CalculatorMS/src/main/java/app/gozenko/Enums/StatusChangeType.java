package app.gozenko.Enums;

public enum StatusChangeType {
    AUTOMATIC("Автоматически"),
    MANUAL("Вручную");

    private final String title;

    StatusChangeType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}