package com.mexico.sas.admin.api.dto.user;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserIdsDto implements Serializable {

    private static final long serialVersionUID = 2652516562907210941L;

    private Long id;
    private Long employeeId;
    private Long roleId;
}
