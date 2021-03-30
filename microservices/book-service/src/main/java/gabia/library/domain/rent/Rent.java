package gabia.library.domain.rent;

import gabia.library.domain.book.Book;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String bookTitle;

    @Column(nullable = false)
    private String bookAuthor;

    @Column(nullable = false)
    private String identifier;      // 유저 아이디

    @Enumerated(EnumType.STRING)
    private RentStatus rentStatus;

    @Column
    private LocalDate rentExpiredDate;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void extendRent(Book book) {
        this.rentExpiredDate = book.getRentExpiredDate();
    }

    public void returnBook() {
        this.rentStatus = RentStatus.RETURN;
    }

    public boolean isInvalidStatus() {
        return this.getRentStatus() == RentStatus.OVERDUE || this.getRentStatus() == RentStatus.RETURN;
    }
}
