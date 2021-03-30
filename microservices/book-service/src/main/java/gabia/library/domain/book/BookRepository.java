package gabia.library.domain.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllByIsDeleted(boolean b, Pageable pageable);

    Optional<Book> findByIdAndIsDeleted(Long id, boolean b);

    List<Book> findTop10ByOrderByCreatedDateDesc();

    List<Book> findTop10ByOrderByReviewCountDesc();

    Page<Book> findByTitleIsContaining(String keyword, Pageable createdDate);

}
