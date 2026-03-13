package app.gozenko.enums;

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
