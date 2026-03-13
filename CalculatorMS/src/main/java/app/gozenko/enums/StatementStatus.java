package app.gozenko.enums;

public enum StatementStatus {
    CREATED("Созднана"),
    PROCESSING("В обработке"),
    APPROVED("Принята"),
    REJECTED("Отклонена"),
    COMPLETED("Выполнена"),
    CANCELLED("Закрыта");

    private final String title;

    StatementStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
