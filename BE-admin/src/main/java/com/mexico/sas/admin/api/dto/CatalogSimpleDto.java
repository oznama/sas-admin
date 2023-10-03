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
public class CatalogSimpleDto implements Serializable {

    private static final long serialVersionUID = 8823338038729389621L;

    @NotNull(message = "{validation.field.required}")
    private Long id;
    private String description;
}
