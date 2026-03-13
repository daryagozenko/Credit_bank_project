package app.gozenko.enums;

public enum Position {
    WORKER("Рабочий"),
    MANAGER("Менеджер среднего звена"),
    TOP_MANAGER("Топ менеджер");

    private final String title;

    Position(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
