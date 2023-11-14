package com.mexico.sas.admin.api.dto.user.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Oziel Naranjo
 */
@Setter
@Getter
@ToString
public class RetrivePasswordDto implements Serializable {

    private static final long serialVersionUID = -1530035039377933085L;

    @NotBlank(message = "{validation.field.required}")
    @Email(message = "{validation.email.format.invalid}")
    private String email;
}
