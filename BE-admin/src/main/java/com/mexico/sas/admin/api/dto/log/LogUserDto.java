package com.mexico.sas.admin.api.dto.log;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
public class LogUserDto implements Serializable {

    private static final long serialVersionUID = 4292128792408322138L;

    private Long id;
    private String email;
    private String name;
    private String image;
}
