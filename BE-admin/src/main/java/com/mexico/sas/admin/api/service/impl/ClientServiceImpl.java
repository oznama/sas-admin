package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.dto.ClientFindDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Client;
import com.mexico.sas.admin.api.repository.ClientRepository;
import com.mexico.sas.admin.api.service.ClientService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientServiceImpl extends Utils implements ClientService {

    @Autowired
    private ClientRepository repository;

    @Override
    public ClientFindDto findById(Long id) throws CustomException {
        Client client = repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.CLIENT_NOT_FOUND, id)));
        return from_M_To_N(client, ClientFindDto.class);
    }
}
