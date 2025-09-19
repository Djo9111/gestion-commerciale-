package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.entity.Objective;
import cds.dsi.gestion_commerciale.repository.ObjectiveRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    public void importObjectives(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // première feuille
            Iterator<Row> rows = sheet.iterator();

            List<Objective> objectives = new ArrayList<>();

            if (rows.hasNext()) rows.next(); // sauter l'en-tête

            while (rows.hasNext()) {
                Row row = rows.next();
                Objective obj = new Objective();

                obj.setNomUtilisateur(getStringCell(row, 0)); // Nom d'utilisateur
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
                obj.setDateCreation(LocalDate.now());
                obj.setActif(true);

                objectives.add(obj);
            }

            objectiveRepository.saveAll(objectives);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'import des objectifs Excel: " + e.getMessage(), e);
        }
    }

    private String getStringCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            return cell != null ? cell.toString().trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getIntegerCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            return Integer.parseInt(cell.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal getBigDecimalCell(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) return null;
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            }
            return new BigDecimal(cell.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}
