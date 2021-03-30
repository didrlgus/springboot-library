package gabia.library.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByIsDeletedAndIdentifier(boolean b, String identifier, Pageable createdDate);

    Page<Review> findAllByIsDeletedAndBookId(boolean b, Long bookId, Pageable createdDate);

    Optional<Review> findByIdAndIsDeleted(Long reviewId, boolean b);
}
