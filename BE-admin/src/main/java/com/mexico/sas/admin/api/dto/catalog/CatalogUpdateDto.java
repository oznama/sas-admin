package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
@FieldNameConstants
public class CatalogUpdateDto implements Serializable {

    private static final long serialVersionUID = -401711392135124332L;

    private Long id;

    @Size(max = 255, message = "{validation.value.invalid}")
    private String value;

    @Size(max = 255, message = "{validation.value.invalid}")
    private String description;
    private Boolean isRequired;

    @Valid
    private CatalogSimpleDto type;

    private List<CatalogSimpleDto> modules;

}
