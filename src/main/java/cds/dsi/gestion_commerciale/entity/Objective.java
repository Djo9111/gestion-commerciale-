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
@Table(name = "objectives")
@Data
public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomUtilisateur;
    private Integer objectifHebdoPlanContact;
    private Integer objectifHebdoConquete;
    private Integer objectifHebdoMonetique;
    private Integer objectifHebdoPackages;
    private BigDecimal objectifMensuelDepots;
    private BigDecimal objectifHebdoDepot;
    private BigDecimal objectifMensuelCreditConso;
    private BigDecimal objectifHebdoCreditConso;
    private BigDecimal objectifMensuelCreditImmo;
    private BigDecimal objectifHebdoCreditImmo;
    private LocalDate dateCreation;
    private Boolean actif;
}