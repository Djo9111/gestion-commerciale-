package cds.dsi.gestion_commerciale.repository;

import cds.dsi.gestion_commerciale.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, String> {
    Optional<Manager> findByNomUtilisateur(String nomUtilisateur);
}
