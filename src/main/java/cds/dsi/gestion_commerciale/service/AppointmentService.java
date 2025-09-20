package cds.dsi.gestion_commerciale.service;


import cds.dsi.gestion_commerciale.entity.Appointment;
import cds.dsi.gestion_commerciale.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    // Create / Update
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
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
