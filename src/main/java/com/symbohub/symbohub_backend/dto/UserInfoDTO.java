package com.symbohub.symbohub_backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String role;
    private Long collegeId; // For department users
    private String collegeName; // For department users
}