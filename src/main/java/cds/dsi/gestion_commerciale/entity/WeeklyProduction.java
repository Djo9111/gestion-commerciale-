package cds.dsi.gestion_commerciale.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_production")
@Data
public class WeeklyProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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