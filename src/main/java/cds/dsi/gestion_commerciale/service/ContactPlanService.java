package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.UploadResultDTO;
import cds.dsi.gestion_commerciale.entity.ContactPlan;
import cds.dsi.gestion_commerciale.repository.ContactPlanRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactPlanService {

    private final ContactPlanRepository contactPlanRepository;

    public UploadResultDTO importContactPlans(MultipartFile file) throws Exception {
        UploadResultDTO result = new UploadResultDTO();
        result.setInserted(0);
        result.setUpdated(0);
        result.setErrors(new ArrayList<>());

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // on saute l'entête
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String codeAgence = getCellValueAsString(row.getCell(0));
                    String agence = getCellValueAsString(row.getCell(1));
                    String motifContact = getCellValueAsString(row.getCell(2));
                    String nomUtilisateur = getCellValueAsString(row.getCell(3));
                    String nomGestionnaire = getCellValueAsString(row.getCell(4));
                    String fdc = getCellValueAsString(row.getCell(5));
                    String numeroClient = getCellValueAsString(row.getCell(6));
                    String client = getCellValueAsString(row.getCell(7));
                    LocalDate dateEntree = parseDate(row.getCell(8));
                    String nationalite = getCellValueAsString(row.getCell(9));
                    String segmentClient = getCellValueAsString(row.getCell(10));
                    String profession = getCellValueAsString(row.getCell(11));
                    String email = getCellValueAsString(row.getCell(12));
                    String telephone = getCellValueAsString(row.getCell(13));

                    // Vérifier si le contact existe déjà (même FDC + numéro client)
                    Optional<ContactPlan> existingOpt = contactPlanRepository
                            .findByFdcAndNumeroClient(fdc, numeroClient);

                    if (existingOpt.isPresent()) {
                        ContactPlan existing = existingOpt.get();
                        existing.setCodeAgence(codeAgence);
                        existing.setAgence(agence);
                        existing.setMotifDeContact(motifContact);
                        existing.setNomUtilisateur(nomUtilisateur);
                        existing.setNomGestionnaire(nomGestionnaire);
                        existing.setClient(client);
                        existing.setDateEntreeRelation(dateEntree);
                        existing.setNationalite(nationalite);
                        existing.setSegmentClient(segmentClient);
                        existing.setProfession(profession);
                        existing.setEmail(email);
                        existing.setTelephone(telephone);
                        contactPlanRepository.save(existing);
                        result.setUpdated(result.getUpdated() + 1);
                    } else {
                        ContactPlan cp = new ContactPlan();
                        cp.setCodeAgence(codeAgence);
                        cp.setAgence(agence);
                        cp.setMotifDeContact(motifContact);
                        cp.setNomUtilisateur(nomUtilisateur);
                        cp.setNomGestionnaire(nomGestionnaire);
                        cp.setFdc(fdc);
                        cp.setNumeroClient(numeroClient);
                        cp.setClient(client);
                        cp.setDateEntreeRelation(dateEntree);
                        cp.setNationalite(nationalite);
                        cp.setSegmentClient(segmentClient);
                        cp.setProfession(profession);
                        cp.setEmail(email);
                        cp.setTelephone(telephone);
                        contactPlanRepository.save(cp);
                        result.setInserted(result.getInserted() + 1);
                    }

                } catch (Exception e) {
                    result.getErrors().add("Ligne " + (i + 1) + " : " + e.getMessage());
                }
            }
        }

        return result;
    }

    // ------------------ Helpers ------------------

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            return LocalDate.parse(cell.getStringCellValue());
        } else {
            return null;
        }
    }
}
