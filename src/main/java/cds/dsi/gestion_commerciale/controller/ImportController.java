package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.dto.PersistenceResultDTO;
import cds.dsi.gestion_commerciale.service.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ImportController {

    private final ObjectiveService objectiveService;

    @PostMapping("/objectives")
    public ResponseEntity<PersistenceResultDTO> importObjectives(@RequestParam("file") MultipartFile file) {
        try {
            PersistenceResultDTO result = objectiveService.importObjectives(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // On renvoie un DTO avec l'erreur capturée pour garder le même format
            PersistenceResultDTO errorResult = new PersistenceResultDTO();
            errorResult.getErrors().add("Erreur lors de l'import : " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}
