package com.technomancarai.tms.mapper;

import com.technomancarai.tms.dto.response.OtpResponse;
import com.technomancarai.tms.entity.OtpVerification;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface OtpVerificationMapper {

    @Mapping(target = "message", source = "message")
    OtpResponse toOtpResponse(OtpVerification otpVerification, String message);
}
