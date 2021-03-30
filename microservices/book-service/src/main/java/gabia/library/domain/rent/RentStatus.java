package gabia.library.domain.rent;

public enum RentStatus {

    RENT("대여중"),
    RETURN("대여가능"),
    OVERDUE("연체중");

    RentStatus(String value) {
        this.value = value;
    }

    private final String value;
    public String value() { return value; }

}
