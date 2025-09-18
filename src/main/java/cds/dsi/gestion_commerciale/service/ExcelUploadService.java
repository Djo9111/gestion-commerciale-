package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.dto.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ExcelUploadService {
    List<ExcelRowDTO> parseExcelFile(MultipartFile file) throws Exception;
    List<ExcelRowDTO> parseExcelFile(InputStream is) throws Exception;

    /**
     * Parse + validate + insert (tolérant : insère les lignes valides, retourne la liste d'erreurs pour les invalides)
     */
    UploadResultDTO processExcelFile(MultipartFile file) throws Exception;
}
