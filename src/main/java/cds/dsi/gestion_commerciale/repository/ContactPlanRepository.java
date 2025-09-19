package cds.dsi.gestion_commerciale.repository;

import cds.dsi.gestion_commerciale.entity.ContactPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactPlanRepository extends JpaRepository<ContactPlan, Long> {

    Optional<ContactPlan> findByNumeroClient(String numeroClient);

    Optional<ContactPlan> findByNumeroClientAndMotifDeContact(String numeroClient, String motifDeContact);

    // Nouvelle méthode pour vérifier si le contact existe déjà (FDC + numéro client)
    Optional<ContactPlan> findByFdcAndNumeroClient(String fdc, String numeroClient);
}
