package gabia.library.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    Optional<BookRequest> findByIdAndIsDeleted(Long id, boolean b);
}
