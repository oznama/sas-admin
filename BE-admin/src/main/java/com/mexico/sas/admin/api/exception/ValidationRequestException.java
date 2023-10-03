package com.mexico.sas.admin.api.exception;

import com.mexico.sas.admin.api.dto.ResponseErrorDetailDto;
import lombok.Getter;

import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Getter
public class ValidationRequestException extends CustomException {

    private List<ResponseErrorDetailDto> errors;

    public ValidationRequestException(List<ResponseErrorDetailDto> errors) {
        super("");
        this.errors = errors;
    }
}
