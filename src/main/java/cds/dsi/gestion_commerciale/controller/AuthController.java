package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.dto.LoginRequest;
import cds.dsi.gestion_commerciale.dto.PerformanceDto;
import cds.dsi.gestion_commerciale.service.AuthService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // === LOGIN ===
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Map<String, String> result = authService.authenticate(loginRequest);
        boolean ok = "Connexion réussie !".equals(result.get("message"));
        return ok ? ResponseEntity.ok(result) : ResponseEntity.status(401).body(result);
    }

    // === INFOS UTILISATEUR ===
    @GetMapping("/user/{nomUtilisateur}")
    public ResponseEntity<?> getUserInfo(@PathVariable String nomUtilisateur) {
        Map<String, String> result = authService.getUserInfo(nomUtilisateur);
        boolean found = result.containsKey("nomComplet");
        return found ? ResponseEntity.ok(result) : ResponseEntity.status(404).body(result);
    }

    // === PERFORMANCE UTILISATEUR ===
    @GetMapping("/performance/{nomUtilisateur}")
    public ResponseEntity<?> getPerformanceData(
            @PathVariable String nomUtilisateur,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfWeek,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endOfWeek
    ) {
        // Valeurs par défaut (tes dates de test)
        if (startOfWeek == null) startOfWeek = LocalDate.of(2025, 1, 6);
        if (endOfWeek == null)   endOfWeek   = LocalDate.of(2025, 1, 12);

        List<PerformanceDto> data = authService.getPerformance(nomUtilisateur, startOfWeek, endOfWeek);
        if (data.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.singletonMap("message",
                    "Aucune donnée de performance ou objectif trouvé pour cet utilisateur."));
        }
        return ResponseEntity.ok(data);
    }
}
