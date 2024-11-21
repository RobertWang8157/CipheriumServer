package com.cipher.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDto {
    private String officerCode;
    private String officerNameEng;
    private String password;
    private String newPassword;
    private String token;
    private Boolean isNeedResetPassword;
    private Long tokenExpireMin;
    private String officerGroupCode;
    private Boolean isSys;
    private Boolean isDpOfficer;

    private String uid;
    private String location;

    public AuthDto(){

    }

    public AuthDto(String location){
        setLocation(location);
    }

    public AuthDto(String token, Boolean isNeedResetPassword){
        setToken(token);
        setIsNeedResetPassword(isNeedResetPassword);
    }
}
