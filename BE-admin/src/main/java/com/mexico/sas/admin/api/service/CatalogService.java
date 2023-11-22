package com.mexico.sas.admin.api.service;

import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.dto.catalog.CatalogDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogPaggedDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogSimpleDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.model.Catalog;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Oziel Naranjo
 */
public interface CatalogService {

    CatalogDto save(CatalogDto catalogDto) throws CustomException;
    CatalogDto update(Long id, CatalogUpdateDto catalogDto) throws CustomException;
    void updateStatus(UpdateStatusDto updateStatusDto) throws CustomException;
    void delete(Long id) throws CustomException;
    Catalog findEntityById(Long id) throws CustomException;
    Catalog findEntityByIdAndCatalogParentId(Long id, Long catalogParentId) throws CustomException;
    CatalogDto findById(Long id) throws CustomException;
    List<CatalogPaggedDto> findAll() throws CustomException;
    CatalogDto findByIdAndCatalogParent(Long id, Long catalogParentId) throws CustomException;
    List<CatalogDto> findChildsDto(Long id) throws CustomException;
    CatalogSimpleDto getCatalogSimpleDto(Long id);
}
