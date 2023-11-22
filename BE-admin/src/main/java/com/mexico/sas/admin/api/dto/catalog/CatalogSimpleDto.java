package com.mexico.sas.admin.api.dto.catalog;

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
public class CatalogSimpleDto implements Serializable {

    private static final long serialVersionUID = 8823338038729389621L;

    private Long id;
    private String value;
}
