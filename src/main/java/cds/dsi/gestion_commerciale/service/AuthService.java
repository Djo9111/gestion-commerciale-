package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.LoginRequest;
import cds.dsi.gestion_commerciale.dto.PerformanceDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AuthService {
    Map<String, String> authenticate(LoginRequest loginRequest);
    Map<String, String> getUserInfo(String nomUtilisateur);
    List<PerformanceDto> getPerformance(String nomUtilisateur, LocalDate startOfWeek, LocalDate endOfWeek);

    // Nouvelle méthode
    List<Map<String, String>> getAllUsers();
}
