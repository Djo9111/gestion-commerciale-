package cds.dsi.gestion_commerciale.repository;

import cds.dsi.gestion_commerciale.entity.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    Optional<Objective> findByNomUtilisateurAndActifTrue(String nomUtilisateur);
}