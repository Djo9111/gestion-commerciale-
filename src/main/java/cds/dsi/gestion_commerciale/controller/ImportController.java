package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.service.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ObjectiveService objectiveService;

    @PostMapping("/objectives")
    public ResponseEntity<String> importObjectives(@RequestParam("file") MultipartFile file) {
        try {
            objectiveService.importObjectives(file);
            return ResponseEntity.ok("Import des objectifs Excel effectué avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}

