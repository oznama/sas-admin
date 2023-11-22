package com.mexico.sas.admin.api.dto.log;

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
public class LogMovementDto implements Serializable {

    private static final long serialVersionUID = 8258738013793355521L;

    private Long id;
    private String userName;
    private String date;
    private String event;
    private String description;
}
