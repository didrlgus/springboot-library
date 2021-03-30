package gabia.library.domain;

public enum Status {
    REQUESTED("요청중"),
    CANCELED("취소"),
    BUYING("구매중"),
    COMPLETED("구매완료");

    Status(String value) { this.value = value; }
    private final String value;
    public String value() { return value; }
}
