package app.gozenko.repository;

import app.gozenko.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM client WHERE passport->>'series' = :series)",
            nativeQuery = true)
    boolean existsByPassportSeries(@Param("series") String passportSeries);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM client WHERE passport->>'number' = :number)",
            nativeQuery = true)
    boolean existsByPassportNumber(@Param("number") String passportNumber);
}
