package com.mexico.sas.admin.api.dto.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author Oziel Naranjo
 */
@Getter
@Setter
@ToString
public class RolePermissionsEnaDisDto {

    @NotNull
    private Long id;
    @NotNull
    private Boolean active;
}
