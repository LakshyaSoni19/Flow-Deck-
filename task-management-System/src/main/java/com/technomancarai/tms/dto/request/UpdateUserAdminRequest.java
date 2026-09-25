package com.technomancarai.tms.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserAdminRequest {

    @Size(max = 45, message = "First name must not exceed 45 characters")
    private String firstName;

    @Size(max = 45, message = "Last name must not exceed 45 characters")
    private String lastName;

    @Size(max = 45, message = "Mobile must not exceed 45 characters")
    private String mobile;

    @Size(max = 45, message = "Gender must not exceed 45 characters")
    private String gender;

    private LocalDate dob;

    @Size(max = 45, message = "Address must not exceed 45 characters")
    private String address;

    private String profileImage;

    private Long cityId;
    private Long departmentId;
    private Long designationId;
}
