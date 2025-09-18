package cds.dsi.gestion_commerciale.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String nomUtilisateur;
    private String motDePasse;
}
