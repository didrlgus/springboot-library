package gabia.library.utils.alert;

public enum AlertType {

    REQUESTED("도서 신청완료"),
    DISPLAY_COMPLETED("도서 구매완료"),
    RENT("도서 대여"),
    RETURN_ONE_DAY_BEFORE("도서 반납 하루 전"),
    RETURN("도서 반납");

    AlertType(String value) {
        this.value = value;
    }

    private final String value;
    public String value() { return value; }

}
