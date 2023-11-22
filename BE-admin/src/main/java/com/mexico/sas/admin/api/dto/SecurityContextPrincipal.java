package com.mexico.sas.admin.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecurityContextPrincipal {
    private Long userId;
    private String name;
    private Long roleId;
    private Long companyId;

    public SecurityContextPrincipal(Long userId) {
        this.userId = userId;
    }
}
