package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.config.EmailUtil;
import cds.dsi.gestion_commerciale.entity.Appointment;
import cds.dsi.gestion_commerciale.entity.ContactPlan;
import cds.dsi.gestion_commerciale.repository.AppointmentRepository;
import cds.dsi.gestion_commerciale.repository.ContactPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ContactPlanRepository contactPlanRepository;
    private final EmailUtil emailUtil;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ContactPlanRepository contactPlanRepository,
                              EmailUtil emailUtil) {
        this.appointmentRepository = appointmentRepository;
        this.contactPlanRepository = contactPlanRepository;
        this.emailUtil = emailUtil;
    }

    // Create / Update
    public Appointment saveAppointment(Appointment appointment) {
        boolean isNew = (appointment.getId() == null);

        Appointment saved = appointmentRepository.save(appointment);

        // Envoi de mail UNIQUEMENT à la création
        if (isNew) {
            try {
                ContactPlan cp = null;

                // contactPlanId est un Integer => convertir en Long
                Integer cpIdInt = saved.getContactPlanId();
                if (cpIdInt != null) {
                    Long cpId = cpIdInt.longValue();
                    cp = contactPlanRepository.findById(cpId).orElse(null);
                }

                // fallback par numéro client si pas de CP via id
                if (cp == null && saved.getNumeroClient() != null) {
                    cp = contactPlanRepository.findByNumeroClient(saved.getNumeroClient()).orElse(null);
                }

                if (cp != null
                        && cp.getEmail() != null
                        && !cp.getEmail().isBlank()
                        && saved.getDateRdv() != null) {

                    final String to = cp.getEmail();
                    final String clientNom = Optional.ofNullable(cp.getClient()).orElse("Client");
                    final String numeroClient =
                            Optional.ofNullable(saved.getNumeroClient())
                                    .orElse(Optional.ofNullable(cp.getNumeroClient()).orElse("—"));
                    final int duree = Optional.ofNullable(saved.getDureeMinutes()).orElse(30);
                    final String objet = saved.getObjetRdv();
                    final String commentaires = saved.getCommentaires();

                    emailUtil.sendAppointmentNotification(
                            to,
                            clientNom,
                            numeroClient,
                            saved.getDateRdv(),
                            duree,
                            objet,
                            commentaires
                    );
                }
            } catch (Exception e) {
                // On log mais on ne bloque pas la création si l'email échoue
                System.err.println("Envoi email RDV échoué : " + e.getMessage());
            }
        }

        return saved;
    }

    // Read
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByManager(String nomUtilisateur) {
        return appointmentRepository.findByNomUtilisateur(nomUtilisateur);
    }

    public List<Appointment> getAppointmentsByClient(String numeroClient) {
        return appointmentRepository.findByNumeroClient(numeroClient);
    }

    // Delete
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}
