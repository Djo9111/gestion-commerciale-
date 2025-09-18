package cds.dsi.gestion_commerciale.repository;


import cds.dsi.gestion_commerciale.entity.WeeklyProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeeklyProductionRepository extends JpaRepository<WeeklyProduction, Long> {
    Optional<WeeklyProduction> findByNomUtilisateurAndDateDebutAndDateFin(
            String nomUtilisateur, LocalDate dateDebut, LocalDate dateFin);
    Optional<WeeklyProduction> findByNomUtilisateurAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(
            String nomUtilisateur, LocalDate dateDebut, LocalDate dateFin);

}
