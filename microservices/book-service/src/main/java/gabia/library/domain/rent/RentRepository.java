package gabia.library.domain.rent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentRepository extends JpaRepository<Rent, Long> {

    Page<Rent> findAllByIdentifier(String identifier, Pageable pageable);

    List<Rent> findAllByIdentifier(String identifier);
}
