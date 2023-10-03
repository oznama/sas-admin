package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
public class LogSecurityDto implements Serializable {
    private static final long serialVersionUID = 3767891652470435136L;

    private List<LogUserDto> access;
    private Page<LogMovementDto> modifications;
}
