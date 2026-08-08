package com.technomancarai.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {

    private String email;
    private String purpose;
    private Boolean isVerified;
    private String message;
}
