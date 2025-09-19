package cds.dsi.gestion_commerciale.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_plans")
@Data
public class ContactPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeAgence;
    private String agence;
    private String motifDeContact;
    private String nomUtilisateur;
    private String nomGestionnaire;
    private String fdc;
    private String numeroClient;
    private String client;
    private LocalDate dateEntreeRelation;
    private String nationalite;
    private String segmentClient;
    private String profession;
    private String email;
    private String telephone;
    private String statutContact = "A_CONTACTER";
    private LocalDateTime dateDernierContact;
    private LocalDateTime createdAt = LocalDateTime.now();
}
