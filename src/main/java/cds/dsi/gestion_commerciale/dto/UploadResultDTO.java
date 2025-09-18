package cds.dsi.gestion_commerciale.dto;

import lombok.Data;
import java.util.List;

@Data
public class UploadResultDTO {
    private int inserted;   // nombre d'inserts
    private int updated;    // nombre d'updates
    private List<String> errors; // erreurs rencontrées
}
