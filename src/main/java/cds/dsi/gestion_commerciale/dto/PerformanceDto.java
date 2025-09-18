package cds.dsi.gestion_commerciale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDto {
    private String indicateur;
    private double realisation;
    private double objectif;
    private double tauxAtteinte;
}