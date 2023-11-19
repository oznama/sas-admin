package com.mexico.sas.admin.api.dto.user;

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
public class UserEnaDisDto implements Serializable {

    private static final long serialVersionUID = 1868561326486981320L;

    private Boolean lock;
}
