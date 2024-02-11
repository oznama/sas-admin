package com.mexico.sas.admin.api.dto.employee;

import com.mexico.sas.admin.api.constants.Regex;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@ToString
@FieldNameConstants
public class EmployeeUpdateDto implements Serializable {
    private static final long serialVersionUID = -2193673364162609612L;
    private Long id;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.format.invalid}")
    private String email;

    @NotBlank(message = "{validation.name.required}")
    @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
    private String name;

    @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
    private String secondName;

    @NotBlank(message = "{validation.lastname.required}")
    @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
    private String surname;

    @Pattern(regexp = Regex.ONLY_LETTERS_WHIT_SPACE, message = "{validation.format.invalid} {validation.only.letter}")
    private String secondSurname;

//    @NotBlank(message = "{validation.phone.required}")
//    @Pattern(regexp = Regex.PHONE_NUMBER, message = "{validation.format.invalid} {validation.only.number}")
    private String phone;
    private String image;

    private Long companyId;
    private Long positionId;
    private Long bossId;
    private Boolean active;
    private String cellphone;
    private String country;
    private String city;
    private String ext;
}
