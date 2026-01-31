package com.symbohub.symbohub_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollegeUpdateDTO {

    @Size(max = 100, message = "College name cannot exceed 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    private String currentPassword;
    private String newPassword;
}