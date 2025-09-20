package cds.dsi.gestion_commerciale.controller;


import cds.dsi.gestion_commerciale.entity.Appointment;
import cds.dsi.gestion_commerciale.service.AppointmentService;
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

    // UPDATE
    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentService.getAppointmentById(id).map(appointment -> {
            appointment.setContactPlanId(appointmentDetails.getContactPlanId());
            appointment.setNomUtilisateur(appointmentDetails.getNomUtilisateur());
            appointment.setNumeroClient(appointmentDetails.getNumeroClient());
            appointment.setDateRdv(appointmentDetails.getDateRdv());
            appointment.setDureeMinutes(appointmentDetails.getDureeMinutes());
            appointment.setTypeRdv(appointmentDetails.getTypeRdv());
            appointment.setObjetRdv(appointmentDetails.getObjetRdv());
            appointment.setStatutRdv(appointmentDetails.getStatutRdv());
            appointment.setCommentaires(appointmentDetails.getCommentaires());
            return appointmentService.saveAppointment(appointment);
        }).orElseThrow(() -> new RuntimeException("RDV non trouvé avec id " + id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }
}
