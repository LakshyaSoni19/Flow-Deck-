package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String gender;
    private LocalDate dob;
    private String address;
    private Boolean isActive;
    private String profileImage;
    private String cityName;
    private String departmentName;
    private String designationName;
    private List<String> roles;
    private String approvalStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
