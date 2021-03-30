package gabia.library.kafka.status;

public enum ReviewStatus {

    PENDING("리뷰 추가 대기중"),
    COMPLETED("리뷰 추가 완료"),
    CANCELED("리뷰 추가 실패");

    ReviewStatus(String value) {
        this.value = value;
    }

    private final String value;
    public String value() { return value; }

}
