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
public class CooperativeDTO {
    
    private Long id;
    
    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 50, message = "Le code ne peut pas dépasser 50 caractères")
    private String code;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;
    
    @Size(max = 30, message = "Le téléphone ne peut pas dépasser 30 caractères")
    private String telephone;
    
    @Email(message = "L'email doit être valide")
    @Size(max = 120, message = "L'email ne peut pas dépasser 120 caractères")
    private String email;
    
    private String adresse;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
