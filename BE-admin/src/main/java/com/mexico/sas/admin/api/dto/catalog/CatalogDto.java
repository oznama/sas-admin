package com.mexico.sas.admin.api.dto.catalog;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Size;
import java.io.Serializable;

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
    private Long catalogParent;


}
