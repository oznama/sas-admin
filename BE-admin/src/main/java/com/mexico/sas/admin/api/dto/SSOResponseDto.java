package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
public class SSOResponseDto implements Serializable {

    private static final long serialVersionUID = -1827498280100936307L;

    private String accessToken;
    private SSOUserDto user;
}
