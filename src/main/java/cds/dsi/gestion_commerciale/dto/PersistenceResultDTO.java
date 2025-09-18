package cds.dsi.gestion_commerciale.dto;

import lombok.Data;
import java.util.List;

@Data
public class PersistenceResultDTO {
    private int inserted;
    private int updated;
    private List<String> errors; // erreurs rencontrées pendant la persistence (par ligne)
}
