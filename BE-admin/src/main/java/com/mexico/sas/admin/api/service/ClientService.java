package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.ClientFindDto;
import com.mexico.sas.admin.api.exception.CustomException;

public interface ClientService {
    ClientFindDto findById(Long id) throws CustomException;
}
