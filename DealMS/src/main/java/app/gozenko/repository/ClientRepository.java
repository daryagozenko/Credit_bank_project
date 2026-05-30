package app.gozenko.repository;

import app.gozenko.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM client WHERE passport->>'series'" +
            " = :series AND passport->>'number' = :number)",
            nativeQuery = true)
    boolean existsByPassportSeriesAndNumber(@Param("series") String passportSeries,
                                            @Param("number") String passportNumber);
}
