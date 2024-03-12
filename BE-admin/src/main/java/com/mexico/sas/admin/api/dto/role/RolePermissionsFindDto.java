package com.mexico.sas.admin.api.dto.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RolePermissionsFindDto {
    private Long id;
    private Long permissionId;
    private Long roleId;
    private Boolean active;
    private Boolean eliminate;
}
