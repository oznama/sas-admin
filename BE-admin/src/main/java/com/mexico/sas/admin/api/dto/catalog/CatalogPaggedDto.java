package com.mexico.sas.admin.api.dto.catalog;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
public class CatalogPaggedDto implements Serializable {

    private static final long serialVersionUID = -880956212549195671L;

    private Long id;
    private String value;
    private String description;
    private Long status;
    private Long companyId;
    private String company;

    private List<CatalogDto> childs;
}
