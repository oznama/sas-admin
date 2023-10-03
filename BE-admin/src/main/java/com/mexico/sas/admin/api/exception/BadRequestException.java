package com.mexico.sas.admin.api.exception;

import lombok.Getter;

/**
 * @author Oziel Naranjo
 */
@Getter
public class BadRequestException extends CustomException {

    private Object detail;

    public BadRequestException(String message, Object detail) {
        super(message);
        this.detail = detail;
    }
}
