package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.dto.UploadResultDTO;
import cds.dsi.gestion_commerciale.entity.WeeklyProduction;
import cds.dsi.gestion_commerciale.repository.ManagerRepository;
import cds.dsi.gestion_commerciale.repository.WeeklyProductionRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelUploadServiceImpl implements ExcelUploadService {

    private final ManagerRepository managerRepository;
    private final WeeklyProductionRepository weeklyProductionRepository;

    @Autowired
    public ExcelUploadServiceImpl(ManagerRepository managerRepository,
                                  WeeklyProductionRepository weeklyProductionRepository) {
        this.managerRepository = managerRepository;
        this.weeklyProductionRepository = weeklyProductionRepository;
    }

    // Constructeur pour tests / main
    public ExcelUploadServiceImpl() {
        this.managerRepository = null;
        this.weeklyProductionRepository = null;
    }

    private static final DateTimeFormatter DTF_DDMMYYYY = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter DTF_YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<ExcelRowDTO> parseExcelFile(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            return parseExcelFile(is);
        }
    }

    @Override
    public List<ExcelRowDTO> parseExcelFile(InputStream is) throws Exception {
        List<ExcelRowDTO> rows = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> it = sheet.iterator();
            if (!it.hasNext()) return rows;

            // Skip header
            it.next();
            int rowNum = 1;
            while (it.hasNext()) {
                Row row = it.next();
                rowNum++;
                if (isRowEmpty(row)) continue;

                ExcelRowDTO dto = new ExcelRowDTO();
                dto.setRowNumber(rowNum);
                dto.setDateDebut(readCellAsLocalDate(row.getCell(0)));
                dto.setDateFin(readCellAsLocalDate(row.getCell(1)));
                dto.setCodeAgence(readCellAsString(row.getCell(2)));
                dto.setFdc(readCellAsString(row.getCell(3)));
                dto.setNombreClientPortefeuille(readCellAsInteger(row.getCell(4)));
                dto.setFdcPrincipale(readCellAsString(row.getCell(5)));
                dto.setNomUtilisateur(readCellAsString(row.getCell(6)));
                dto.setNomGestionnaire(readCellAsString(row.getCell(7)));
                dto.setVenteSecheCarte(readCellAsInteger(row.getCell(8)));
                dto.setPackages(readCellAsInteger(row.getCell(9)));
                dto.setCumulPretImmobilier(readCellAsBigDecimal(row.getCell(10)));
                dto.setCumulCreditConso(readCellAsBigDecimal(row.getCell(11)));
                dto.setCumulDepots(readCellAsBigDecimal(row.getCell(12)));
                dto.setNouveauxComptesOuverts(readCellAsInteger(row.getCell(13)));

                rows.add(dto);
            }
        }

        return rows;
    }

    @Override
    public UploadResultDTO processExcelFile(MultipartFile file) throws Exception {
        List<ExcelRowDTO> parsed = parseExcelFile(file);
        List<String> errors = new ArrayList<>();
        List<WeeklyProduction> toInsert = new ArrayList<>();

        for (ExcelRowDTO dto : parsed) {
            List<String> rowErrors = validateRow(dto);
            if (rowErrors.isEmpty()) {
                WeeklyProduction wp = mapDtoToEntity(dto);
                toInsert.add(wp);
            } else {
                errors.add("Ligne " + dto.getRowNumber() + " : " + String.join("; ", rowErrors));
            }
        }

        if (!toInsert.isEmpty()) {
            if (weeklyProductionRepository == null) {
                System.out.println("[ExcelUploadServiceImpl] Mode test : " + toInsert.size() + " lignes prêtes à l'insertion.");
            } else {
                weeklyProductionRepository.saveAll(toInsert);
            }
        }

        UploadResultDTO res = new UploadResultDTO();
        res.setInserted(toInsert.size()); // utilise inserted
        res.setUpdated(0);                // pas d’update ici (c’est juste insertion simple)
        res.setErrors(errors);
        return res;
    }


    public List<String> validateRowSyntax(ExcelRowDTO r) {
        List<String> errors = new ArrayList<>();
        if (r.getDateDebut() == null) errors.add("Date de debut manquante ou invalide");
        if (r.getDateFin() == null) errors.add("Date de fin manquante ou invalide");
        if (r.getDateDebut() != null && r.getDateFin() != null && r.getDateFin().isBefore(r.getDateDebut()))
            errors.add("Date de fin avant date de debut");
        if (r.getNomUtilisateur() == null || r.getNomUtilisateur().isBlank()) errors.add("Nom d'utilisateur manquant");
        if (r.getPackages() != null && r.getPackages() < 0) errors.add("Packages négatif");
        if (r.getVenteSecheCarte() != null && r.getVenteSecheCarte() < 0) errors.add("Vente seche carte négative");
        if (r.getCumulCreditConso() != null && r.getCumulCreditConso().compareTo(BigDecimal.ZERO) < 0)
            errors.add("Cumul credit conso négatif");
        return errors;
    }

    private List<String> validateRow(ExcelRowDTO r) {
        List<String> errors = validateRowSyntax(r);
        if (managerRepository != null && r.getNomUtilisateur() != null && !r.getNomUtilisateur().isBlank()) {
            boolean exists = managerRepository.findByNomUtilisateur(r.getNomUtilisateur()).isPresent();
            if (!exists) errors.add("Nom d'utilisateur inconnu en base");
        }
        return errors;
    }

    private WeeklyProduction mapDtoToEntity(ExcelRowDTO d) {
        WeeklyProduction w = new WeeklyProduction();
        w.setDateDebut(d.getDateDebut());
        w.setDateFin(d.getDateFin());
        w.setCodeAgence(d.getCodeAgence());
        w.setFdc(d.getFdc());
        w.setNombreClientPortefeuille(d.getNombreClientPortefeuille());
        w.setFdcPrincipale(d.getFdcPrincipale());
        w.setNomUtilisateur(d.getNomUtilisateur());
        w.setNomGestionnaire(d.getNomGestionnaire());
        w.setVenteSecheCarte(d.getVenteSecheCarte());
        w.setPackages(d.getPackages());
        w.setCumulPretImmobilier(d.getCumulPretImmobilier());
        w.setCumulCreditConso(d.getCumulCreditConso());
        w.setCumulDepots(d.getCumulDepots());
        w.setNouveauxComptesOuverts(d.getNouveauxComptesOuverts());
        return w;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = 0; c <= 13; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (cell.getCellType() == CellType.STRING && !cell.getStringCellValue().trim().isEmpty()) return false;
                else if (cell.getCellType() == CellType.NUMERIC) return false;
            }
        }
        return true;
    }

    private String readCellAsString(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case STRING: return cell.getStringCellValue().trim();
                case NUMERIC:
                    double v = cell.getNumericCellValue();
                    if (v == Math.floor(v)) return String.valueOf((long) v);
                    return String.valueOf(v);
                case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try { return cell.getStringCellValue(); } catch (Exception e) { return String.valueOf(cell.getNumericCellValue()); }
                default: return null;
            }
        } catch (Exception e) { return null; }
    }

    private Integer readCellAsInteger(Cell cell) {
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case NUMERIC: return (int) Math.round(cell.getNumericCellValue());
                case STRING:
                    String s = cell.getStringCellValue().trim().replaceAll("\\s", "").replace(",", ".");
                    if (s.isEmpty()) return null;
                    return (int) Math.round(Double.parseDouble(s));
                case FORMULA: return (int) Math.round(cell.getNumericCellValue());
                default: return null;
            }
        } catch (Exception e) { return null; }
    }

    private BigDecimal readCellAsBigDecimal(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        try {
            switch (cell.getCellType()) {
                case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String s = cell.getStringCellValue().trim().replaceAll("\\s", "").replace(",", ".");
                    if (s.isEmpty()) return BigDecimal.ZERO;
                    return new BigDecimal(s);
                case FORMULA: return BigDecimal.valueOf(cell.getNumericCellValue());
                default: return BigDecimal.ZERO;
            }
        } catch (Exception e) { return BigDecimal.ZERO; }
    }

    private LocalDate readCellAsLocalDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                String s = cell.getStringCellValue().trim();
                if (s.isEmpty()) return null;
                try { return LocalDate.parse(s, DTF_DDMMYYYY); } catch (Exception ignored) {}
                try { return LocalDate.parse(s, DTF_YYYYMMDD); } catch (Exception ignored) {}
                try { return LocalDate.parse(s); } catch (Exception ex) { return null; }
            }
        } catch (Exception e) { return null; }
    }
}
