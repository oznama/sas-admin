package com.mexico.sas.admin.api.dto.role;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RoleUpdateDto {
    @NotNull(message = "{validation.field.required}")
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Boolean eliminate;
}
