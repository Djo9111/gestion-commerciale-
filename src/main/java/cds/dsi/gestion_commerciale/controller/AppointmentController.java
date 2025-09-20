package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.entity.Appointment;
import cds.dsi.gestion_commerciale.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*") // pour autoriser frontend
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // CREATE
    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.saveAppointment(appointment);
    }

    // READ
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public Optional<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/manager/{nomUtilisateur}")
    public List<Appointment> getAppointmentsByManager(@PathVariable String nomUtilisateur) {
        return appointmentService.getAppointmentsByManager(nomUtilisateur);
    }

    @GetMapping("/client/{numeroClient}")
    public List<Appointment> getAppointmentsByClient(@PathVariable String numeroClient) {
        return appointmentService.getAppointmentsByClient(numeroClient);
    }

    // UPDATE PARTIEL (PUT tolérant)
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentService.getAppointmentById(id).map(appointment -> {

            // N'écrase PAS avec null : on met à jour seulement si fourni
            if (appointmentDetails.getContactPlanId() != null) {
                appointment.setContactPlanId(appointmentDetails.getContactPlanId());
            }
            if (appointmentDetails.getNomUtilisateur() != null) {
                appointment.setNomUtilisateur(appointmentDetails.getNomUtilisateur());
            }
            if (appointmentDetails.getNumeroClient() != null) {
                appointment.setNumeroClient(appointmentDetails.getNumeroClient());
            }
            if (appointmentDetails.getDateRdv() != null) {
                appointment.setDateRdv(appointmentDetails.getDateRdv());
            }
            if (appointmentDetails.getDureeMinutes() != null) {
                appointment.setDureeMinutes(appointmentDetails.getDureeMinutes());
            }
            if (appointmentDetails.getTypeRdv() != null) {
                appointment.setTypeRdv(appointmentDetails.getTypeRdv());
            }
            if (appointmentDetails.getObjetRdv() != null) {
                appointment.setObjetRdv(appointmentDetails.getObjetRdv());
            }
            if (appointmentDetails.getStatutRdv() != null) {
                appointment.setStatutRdv(appointmentDetails.getStatutRdv());
            }
            if (appointmentDetails.getCommentaires() != null) {
                appointment.setCommentaires(appointmentDetails.getCommentaires());
            }

            // Garde-fou : si la colonne est NOT NULL, ne jamais sauver un null
            if (appointment.getDateRdv() == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "dateRdv est obligatoire pour un RDV"
                );
            }

            return appointmentService.saveAppointment(appointment);
        }).orElseThrow(() -> new RuntimeException("RDV non trouvé avec id " + id));
    }




    // DELETE
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }
}
