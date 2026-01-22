package com.taxi_brousse.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 120, message = "Le nom ne peut pas dépasser 120 caractères")
    private String nom;
    
    @Size(max = 120, message = "Le prénom ne peut pas dépasser 120 caractères")
    private String prenom;
    
    @Size(max = 30, message = "Le téléphone ne peut pas dépasser 30 caractères")
    private String telephone;
    
    @Email(message = "L'email doit être valide")
    @Size(max = 120, message = "L'email ne peut pas dépasser 120 caractères")
    private String email;
    
    @Size(max = 50, message = "Le numéro CIN ne peut pas dépasser 50 caractères")
    private String numeroCin;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
