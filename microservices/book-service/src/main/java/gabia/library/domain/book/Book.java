package gabia.library.domain.book;

import gabia.library.domain.rent.Rent;
import gabia.library.domain.rent.RentStatus;
import gabia.library.dto.BookRequestDto;
import gabia.library.dto.ReviewRequestDto;
import gabia.library.dto.ReviewResponseDto;
import gabia.library.kafka.BookRentMessage;
import gabia.library.kafka.BookReturnMessage;
import gabia.library.kafka.ReviewUpdateOfBookMessage;
import gabia.library.utils.alert.AlertType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long rentId;

    @Column(length = 25)
    private String identifier;      // 유저 아이디

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(length = 25, nullable = false)
    private String publisher;

    @Column(length = 20)
    private String publishDate;

    @Column(length = 50)
    private String category;

    @Lob
    private String intro;

    @Lob
    private String content;

    @Column
    private String referenceUrl;

    @Column(length = 50)
    private String location;

    @Column
    private String thumbnail;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isRent;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isDeleted;

    @Column
    private String etc;

    @Column
    private LocalDate rentExpiredDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer extensionCount;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer totalRating;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private Integer reviewCount;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void update(BookRequestDto.Put bookRequestDto) {
        this.title = bookRequestDto.getTitle();
        this.author = bookRequestDto.getAuthor();
        this.publisher = bookRequestDto.getPublisher();
        this.publishDate = bookRequestDto.getPublishDate();
        this.category = bookRequestDto.getCategory();
        this.intro = bookRequestDto.getIntro();
        this.content = bookRequestDto.getContent();
        this.referenceUrl = bookRequestDto.getReferenceUrl();
        this.location = bookRequestDto.getLocation();
        this.thumbnail = bookRequestDto.getThumbnail();
        this.isRent = bookRequestDto.isRent();
        this.etc = bookRequestDto.getEtc();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void rent(String identifier, Long rentId) {
        this.identifier = identifier;
        this.rentId = rentId;
        this.isRent = true;
        this.rentExpiredDate = LocalDate.now().plusMonths(1);
    }

    public void extendRent() {
        this.extensionCount++;
        this.rentExpiredDate = this.rentExpiredDate.plusMonths(1);
    }

    public void returnBook() {
        this.identifier = null;
        this.rentId = null;
        this.isRent = false;
        this.extensionCount = 0;
        this.rentExpiredDate = null;
    }

    public void addReviewRating(ReviewUpdateOfBookMessage message) {
        if(isNull(this.totalRating)) {
            this.totalRating = 0;
        }
        this.totalRating = this.totalRating + message.getReviewRating();
        this.reviewCount++;
    }

    public ReviewResponseDto toReviewResponseDto(String reviewIdentifier) {
        return ReviewResponseDto.builder()
                .identifier(reviewIdentifier)
                .avgReviewRating(Double.parseDouble(calcAvgReviewRating()))
                .reviewCount(reviewCount)
                .build();
    }

    public String calcAvgReviewRating() {
        if (this.getReviewCount() == 0) {
            return "0.0";
        }
        return String.format("%.1f", (double) this.getTotalRating() / this.getReviewCount());
    }

    public Rent toRent(String identifier, LocalDate rentExpiredDate) {
        return Rent.builder()
                .bookId(this.getId())
                .bookTitle(this.getTitle())
                .bookAuthor(this.getAuthor())
                .identifier(identifier)
                .rentExpiredDate(rentExpiredDate)
                .rentStatus(RentStatus.RENT)
                .build();
    }

    public void deleteReview(ReviewRequestDto reviewRequestDto) {
        this.totalRating = this.totalRating - reviewRequestDto.getRating();
        this.reviewCount--;
    }

    public BookRentMessage toBookRentMessage(String rentIdentifier, String email) {

        return BookRentMessage.builder()
                .bookId(this.getId())
                .bookTitle(this.getTitle())
                .bookAuthor(this.getAuthor())
                .identifier(rentIdentifier)
                .email(email)
                .rentExpiredDate(LocalDate.now().plusMonths(1))
                .alertType(AlertType.RENT)
                .build();
    }

    public BookReturnMessage toBookReturnMessage(String returnIdentifier, Long rentId, String email) {

        return BookReturnMessage.builder()
                .bookId(this.getId())
                .rentId(rentId)
                .bookTitle(this.getTitle())
                .bookAuthor(this.getAuthor())
                .identifier(returnIdentifier)
                .email(email)
                .returnDateTime(LocalDateTime.now())
                .alertType(AlertType.RETURN)
                .build();
    }
}
