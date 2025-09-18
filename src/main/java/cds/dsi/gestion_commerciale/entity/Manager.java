package cds.dsi.gestion_commerciale.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "managers")
@Data
public class Manager {
    @Id
    @Column(name = "nom_utilisateur")
    private String nomUtilisateur;

    private String nom;
    private String email;
    private String fonction;

    @Column(name = "code_agence")
    private String codeAgence;

    private String telephone;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    @Column(name = "porte_feuille")
    private Integer porteFeuille;
}
