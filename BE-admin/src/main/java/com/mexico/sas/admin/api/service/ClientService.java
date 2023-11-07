package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.ClientFindDto;
import com.mexico.sas.admin.api.dto.ClientFindSelectDto;
import com.mexico.sas.admin.api.exception.CustomException;

import java.util.List;

public interface ClientService {
    ClientFindDto findById(Long id) throws CustomException;
    List<ClientFindSelectDto> getForSelect();
}
