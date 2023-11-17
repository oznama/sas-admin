package com.mexico.sas.admin.api.dto.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
public class LogMovementDto implements Serializable {

    private static final long serialVersionUID = 8258738013793355521L;

    private String userName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date date;
    private String event;
    private String detail;
}
