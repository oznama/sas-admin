package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
@FieldNameConstants
public class CatalogDto implements Serializable {

    private static final long serialVersionUID = 3913525122683116977L;

    private Long id;

    @Size(max = 255, message = "{validation.value.invalid}")
    private String value;

    @Size(max = 255, message = "{validation.value.invalid}")
    private String description;
    @NotNull(message = "{validation.field.required}")
    private Boolean isRequired;
    private Long catalogParent;

    @Valid
    @NotNull(message = "{validation.field.required}")
    private CatalogSimpleDto status;

    @Valid
    @NotNull(message = "{validation.field.required}")
    private CatalogSimpleDto type;

    private Date creationDate;
    private Date lastUpdate;

    private List<CatalogSimpleDto> modules;

}
