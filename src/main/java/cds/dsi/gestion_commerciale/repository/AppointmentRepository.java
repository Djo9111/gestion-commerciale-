package cds.dsi.gestion_commerciale.repository;


import cds.dsi.gestion_commerciale.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByNomUtilisateur(String nomUtilisateur);
    List<Appointment> findByNumeroClient(String numeroClient);
    List<Appointment> findByStatutRdv(String statutRdv);
}
