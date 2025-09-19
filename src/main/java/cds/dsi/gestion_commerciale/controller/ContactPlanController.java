package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.dto.UploadResultDTO;
import cds.dsi.gestion_commerciale.entity.ContactPlan;
import cds.dsi.gestion_commerciale.service.ContactPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ContactPlanController {

    private final ContactPlanService contactPlanService;

    /**
     * Import d'un fichier Excel Plan de Contact
     * @param file le fichier Excel à importer
     * @return le nombre d'inserts, updates et éventuelles erreurs
     */
    @PostMapping("/contact-plans")
    public ResponseEntity<UploadResultDTO> importContactPlans(@RequestParam("file") MultipartFile file) {
        try {
            // Appel du service qui fait le parsing Excel et la persistance
            UploadResultDTO result = contactPlanService.importContactPlans(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Retourne une erreur claire en cas de problème
            UploadResultDTO errorResult = new UploadResultDTO();
            errorResult.setInserted(0);
            errorResult.setUpdated(0);
            errorResult.setErrors(java.util.List.of("Erreur: " + e.getMessage()));
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
    @GetMapping("/my-contacts/{nomUtilisateur}")
    public ResponseEntity<?> getMyContacts(@PathVariable String nomUtilisateur) {
        try {
            List<ContactPlan> contacts = contactPlanService.getContactsByUser(nomUtilisateur);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}
