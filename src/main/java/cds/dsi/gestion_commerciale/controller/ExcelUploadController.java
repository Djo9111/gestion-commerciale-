package cds.dsi.gestion_commerciale.controller;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.dto.PersistenceResultDTO;
import cds.dsi.gestion_commerciale.dto.UploadResultDTO;
import cds.dsi.gestion_commerciale.service.ExcelPersistenceService;
import cds.dsi.gestion_commerciale.service.ExcelUploadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:3000")
public class ExcelUploadController {

    @Autowired
    private ExcelUploadServiceImpl excelUploadService;

    @Autowired
    private ExcelPersistenceService excelPersistenceService;

    @PostMapping("/excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 1) parsing du fichier
            List<ExcelRowDTO> parsed = excelUploadService.parseExcelFile(file);

            // 2) validation syntaxique
            List<ExcelRowDTO> validRows = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (ExcelRowDTO r : parsed) {
                List<String> rowErrs = excelUploadService.validateRowSyntax(r);
                if (rowErrs.isEmpty()) {
                    validRows.add(r);
                } else {
                    errors.add("Ligne " + r.getRowNumber() + " : " + String.join("; ", rowErrs));
                }
            }

            // 3) persistance (insert / update)
            PersistenceResultDTO persistRes = excelPersistenceService.persistRows(validRows);

            // 4) fusion des erreurs
            List<String> allErrors = new ArrayList<>();
            allErrors.addAll(errors);
            if (persistRes.getErrors() != null) {
                allErrors.addAll(persistRes.getErrors());
            }

            // 5) construire la réponse finale avec le nouveau DTO unifié
            UploadResultDTO finalRes = new UploadResultDTO();
            finalRes.setInserted(persistRes.getInserted());
            finalRes.setUpdated(persistRes.getUpdated());
            finalRes.setErrors(allErrors);

            return ResponseEntity.ok(finalRes);

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Erreur serveur lors de l'import : " + e.getMessage());
        }
    }
}
