package app.gozenko.Enums;

public enum Gender {
    MALE("М"),
    FEMALE("Ж");

    private final String title;

    Gender(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
