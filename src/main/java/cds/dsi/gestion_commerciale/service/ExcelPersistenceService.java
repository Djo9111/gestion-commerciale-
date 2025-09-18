package cds.dsi.gestion_commerciale.service;

import cds.dsi.gestion_commerciale.dto.ExcelRowDTO;
import cds.dsi.gestion_commerciale.dto.PersistenceResultDTO;

import java.util.List;

public interface ExcelPersistenceService {
    /**
     * Persiste (insert/update) la liste de lignes valides en DB.
     * Retourne un résumé (nombre insérés, mis à jour, erreurs).
     */
    PersistenceResultDTO persistRows(List<ExcelRowDTO> validRows);
}
