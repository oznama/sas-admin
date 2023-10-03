package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
public class UpdateStatusDto implements Serializable {
    private static final long serialVersionUID = -5289763169416964981L;

    @NotNull(message = "{validation.field.required}")
    private Long id;
    @NotNull(message = "{validation.field.required}")
    private Long statusId;
}
