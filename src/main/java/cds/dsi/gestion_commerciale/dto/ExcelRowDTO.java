// ExcelRowDTO.java
package cds.dsi.gestion_commerciale.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExcelRowDTO {
    private int rowNumber;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String codeAgence;
    private String fdc;
    private Integer nombreClientPortefeuille;
    private String fdcPrincipale;
    private String nomUtilisateur;
    private String nomGestionnaire;
    private Integer venteSecheCarte;
    private Integer packages;
    private BigDecimal cumulPretImmobilier;
    private BigDecimal cumulCreditConso;
    private BigDecimal cumulDepots;
    private Integer nouveauxComptesOuverts;
}
