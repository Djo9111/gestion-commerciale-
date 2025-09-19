package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.PersistenceResultDTO;
import cds.dsi.gestion_commerciale.entity.Objective;
import cds.dsi.gestion_commerciale.repository.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    /**
     * Importe un fichier Excel (.xlsx) contenant les objectifs.
     * Pour chaque ligne : si nomUtilisateur actif existe -> update, sinon insert.
     * Retourne un résumé (inserted, updated, erreurs).
     */
    @Transactional
    public PersistenceResultDTO importObjectives(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        List<Objective> toSave = new ArrayList<>();
        int inserted = 0;
        int updated = 0;

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Si tu as une ligne d'en-tête, on la saute
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                int rowNum = row.getRowNum() + 1; // humainement 1-based

                String nomUtilisateur = getStringCell(row, 0);
                if (nomUtilisateur == null || nomUtilisateur.isBlank()) {
                    errors.add("Ligne " + rowNum + " : nom d'utilisateur manquant");
                    continue;
                }

                try {
                    // On récupère l'objectif actif si existant (repo possède findByNomUtilisateurAndActifTrue)
                    Optional<Objective> existingOpt =
                            objectiveRepository.findByNomUtilisateurAndActifTrue(nomUtilisateur);

                    Objective obj;
                    boolean isUpdate = false;

                    if (existingOpt.isPresent()) {
                        obj = existingOpt.get();
                        isUpdate = true;
                    } else {
                        obj = new Objective();
                        obj.setNomUtilisateur(nomUtilisateur);
                        obj.setActif(true);
                        obj.setDateCreation(LocalDate.now());
                    }

                    // Remplir / mettre à jour les champs (colonnes en fonction de ton fichier)
                    // Attention : index des colonnes basé sur l'ordre que tu as indiqué
                    // 0=Nom d'utilisateur, 1=Nom, 2=Email, 3=Fonction, ... jusqu'aux objectifs (col 10..19)
                    obj.setObjectifHebdoPlanContact(getIntegerCell(row, 10));
                    obj.setObjectifHebdoConquete(getIntegerCell(row, 11));
                    obj.setObjectifHebdoMonetique(getIntegerCell(row, 12));
                    obj.setObjectifHebdoPackages(getIntegerCell(row, 13));
                    obj.setObjectifMensuelDepots(getBigDecimalCell(row, 14));
                    obj.setObjectifHebdoDepot(getBigDecimalCell(row, 15));
                    obj.setObjectifMensuelCreditConso(getBigDecimalCell(row, 16));
                    obj.setObjectifHebdoCreditConso(getBigDecimalCell(row, 17));
                    obj.setObjectifMensuelCreditImmo(getBigDecimalCell(row, 18));
                    obj.setObjectifHebdoCreditImmo(getBigDecimalCell(row, 19));

                    toSave.add(obj);
                    if (isUpdate) updated++; else inserted++;

                } catch (Exception e) {
                    errors.add("Ligne " + rowNum + " : erreur traitement -> " + e.getMessage());
                }
            }

            if (!toSave.isEmpty()) {
                objectiveRepository.saveAll(toSave); // JPA fera insert ou update selon id présent
            }

        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de l'import des objectifs Excel: " + ex.getMessage(), ex);
        }

        PersistenceResultDTO res = new PersistenceResultDTO();
        res.setInserted(inserted);
        res.setUpdated(updated);
        res.setErrors(errors);
        return res;
    }

    /* --- Helpers pour lire les cellules --- */
    private String getStringCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
            if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
            return cell.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntegerCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
            String s = cell.toString().trim();
            return s.isEmpty() ? null : Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal getBigDecimalCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) return BigDecimal.valueOf(cell.getNumericCellValue());
            String s = cell.toString().trim().replace(",", "");
            return s.isEmpty() ? null : new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }
}
