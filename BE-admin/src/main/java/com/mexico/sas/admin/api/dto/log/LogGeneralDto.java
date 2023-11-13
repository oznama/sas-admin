package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
public class LogGeneralDto implements Serializable {
    private static final long serialVersionUID = -4326444318211705979L;

    private Long id;
    private String email;
    private String name;
    private String image;
    private String date;
    private String type;
}
