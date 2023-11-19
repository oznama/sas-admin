package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.dto.catalog.CatalogDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogPaggedDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogSimpleDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Catalog;
import com.mexico.sas.admin.api.repository.CatalogRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Oziel Naranjo
 */
@Slf4j
@Service
public class CatalogServiceImpl extends LogMovementUtils implements CatalogService {

    @Autowired
    private CatalogRepository repository;

    @Override
    public CatalogDto save(CatalogDto catalogDto) throws CustomException {
        log.debug("Saving catalog {} ...", catalogDto);
        Catalog catalog = from_M_To_N(catalogDto, Catalog.class);

        validationSave(catalogDto, catalog);

        try{
            repository.save(catalog);
            save(Catalog.class.getSimpleName(), catalog.getId(), CatalogKeys.LOG_DETAIL_INSERT, "TODO");
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.CATALOG_DUPLICATED, catalogDto.getDescription()), catalogDto);
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_CREATED, catalog.getDescription()));
        }
        if(catalog.getId() == null) {
            String msgError = I18nResolver.getMessage(I18nKeys.CATALOG_NOT_CREATED, catalog.getDescription());
            log.error(msgError);
            throw new CustomException(msgError);
        }
        log.debug("Catalog created with id {}", catalog.getId());
        return findById(catalog.getId());
    }

    @Override
    public CatalogDto update(Long id, CatalogUpdateDto catalogDto) throws CustomException {
        catalogDto.setId(id);
        log.debug("Updating catalog {} ...", catalogDto);
        Catalog catalog = validationUpdate(catalogDto);
        try {
            if(catalogDto.getValue() != null && !catalogDto.getValue().equalsIgnoreCase(catalog.getValue()))
                catalog.setValue(catalogDto.getValue());
            if(catalogDto.getDescription() != null && !catalogDto.getDescription().equalsIgnoreCase(catalog.getDescription()))
                catalog.setDescription(catalogDto.getDescription());
            if(catalogDto.getIsRequired() != null && !catalogDto.getIsRequired().equals(catalog.getIsRequired()))
                catalog.setIsRequired(catalogDto.getIsRequired());

            repository.save(catalog);
            save(Catalog.class.getSimpleName(), catalog.getId(), CatalogKeys.LOG_DETAIL_UPDATE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_UPDATED, catalog.getId()));
        }
        return findById(id);
    }

    @Override
    public void updateStatus(UpdateStatusDto updateStatusDto) throws CustomException {
        findById(updateStatusDto.getId());
        Catalog catalogParent = new Catalog();
        catalogParent.setId(CatalogKeys.ESTATUS_MACHINE);
        repository.findByIdAndCatalogParent(updateStatusDto.getStatusId(), catalogParent)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, updateStatusDto.getStatusId())));;
        log.debug("Updating proposal status: {}", updateStatusDto);
        repository.updateStatus(updateStatusDto.getId(), updateStatusDto.getStatusId());
        save(Catalog.class.getSimpleName(), updateStatusDto.getId(),
                updateStatusDto.getStatusId().equals(CatalogKeys.ESTATUS_MACHINE_ELIMINATED) ?
                        CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS, "TODO");
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityById(id);
        try{
            repository.deleteById(id);
            save(Catalog.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
        }
    }

    @Override
    public CatalogDto findById(Long id) throws CustomException {
        return parseCatalogDto(findEntityById(id));
    }

    @Override
    public CatalogDto findByIdAndCatalogParent(Long id, Long catalogParentId) throws CustomException {
        log.debug("Finding catalog with id {} and catalogParentId {}...", id, catalogParentId);
        return parseCatalogDto(findEntityByIdAndCatalogParentId(id, catalogParentId));
    }

    @Override
    public List<CatalogDto> findAll() throws CustomException {
        return parseCatalogDto(repository.findByCatalogParentIsNull());
    }

    @Override
    public List<CatalogDto> findChildsDto(Long id) throws CustomException {
        return parseCatalogDto(repository.findByCatalogParent(findEntityById(id)));
    }

    @Override
    public List<CatalogPaggedDto> findChilds(Long id) throws CustomException {
        return parsingPage(repository.findByCatalogParent(findEntityById(id)));
    }

    @Override
    public List<CatalogPaggedDto> findChilds(List<Long> catalogsParentId) throws CustomException {
        if(!catalogsParentId.isEmpty()) {
            List<Catalog> parents = getCatalogsList(catalogsParentId);
            if(!parents.isEmpty()) {
                return parsingPage(repository.findByCatalogParentIn(parents));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public CatalogDto findByIdInCatalogsParent(Long id, List<Long> catalogsParentId) throws CustomException {
        Catalog catalog = repository.findByIdAndCatalogParentIn(id, getCatalogsList(catalogsParentId)).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, id)));
        return parseCatalogDto(catalog);
    }

    @Override
    public Catalog findEntityById(Long id) throws CustomException {
        return repository.findById(id).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, id)));
    }

    @Override
    public Catalog findEntityByIdAndCatalogParentId(Long id, Long catalogParentId) throws CustomException {
        Catalog parent = new Catalog();
        parent.setId(catalogParentId);
        return repository.findByIdAndCatalogParent(id, parent).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, id)));
    }

    @Override
    public CatalogSimpleDto getCatalogSimpleDto(Long id) {
        CatalogSimpleDto catalogSimpleDto = null;
        try {
            Catalog catalog = findEntityById(id);
            catalogSimpleDto = from_M_To_N(catalog, CatalogSimpleDto.class);
            catalogSimpleDto.setDescription(catalog.getValue());
            return catalogSimpleDto;
        } catch (CustomException e) {
            log.warn("Impossible parsing catalog, error: {}", e.getMessage());
        }
        return catalogSimpleDto;
    }

    private CatalogDto parseCatalogDto(Catalog catalog) throws CustomException {
        CatalogDto catalogDto = from_M_To_N(catalog, CatalogDto.class);
        if(catalog.getCatalogParent() != null && catalog.getCatalogParent().getId() != null)
            catalogDto.setCatalogParent(catalog.getCatalogParent().getId());
        if(catalog.getStatus()!=null) {
            catalogDto.setStatus(getCatalogSimpleDto(catalog.getStatus()));
        }
        if(catalog.getType()!=null) {
            catalogDto.setType(getCatalogSimpleDto(catalog.getType()));
        }
        return catalogDto;
    }

    private CatalogPaggedDto parseCatalogPaggedDto(Catalog catalog) throws CustomException {
        CatalogPaggedDto catalogDto = from_M_To_N(catalog, CatalogPaggedDto.class);
        if(catalog.getStatus()!=null) {
            log.debug("Setting catalog status {}...", catalog.getStatus());
            catalogDto.setStatus(getCatalogSimpleDto(catalog.getStatus()));
        }
        if(catalog.getType()!=null) {
            log.debug("Setting type status {}...", catalog.getType());
            catalogDto.setType(getCatalogSimpleDto(catalog.getType()));
        }
        log.debug("Catalog {} finded!", catalogDto.getId());
        return catalogDto;
    }

    private List<CatalogPaggedDto> parsingPage(List<Catalog> catalogs) {
        List<CatalogPaggedDto> catalogDtos = new ArrayList<>();
        catalogs.forEach(catalog -> {
            try {
                catalogDtos.add(parseCatalogPaggedDto(catalog));
            } catch (CustomException e) {
                log.warn("Catalog with id {} not added to list, error parsing: {}", catalog.getId(), e.getMessage());
            }
        });
        return catalogDtos;
    }

    private void validationSave(CatalogDto catalogDto, Catalog catalog) throws CustomException {
        long nextId = 1;
        if (catalogDto.getCatalogParent() != null) {
            log.debug("Catalog parent not null: {}", catalogDto.getCatalogParent());
            Catalog catalogParent = findEntityById(catalogDto.getCatalogParent());
            catalog.setCatalogParent(catalogParent);
            if (catalogDto.getCatalogParent() > 1000000000l) {
                Optional<Catalog> lastChild = repository.findFirstByCatalogParentOrderByIdDesc(catalogParent);
                if (lastChild.isPresent()) {
                    nextId = lastChild.get().getId() + 1;
                } else {
                    long parentSeq = catalogDto.getCatalogParent() - CatalogKeys.PARENT_BASE;
                    nextId = CatalogKeys.CHILD_BASE + (parentSeq * CatalogKeys.CHILD_SEQ_BASE) + nextId;
                }
            } else {
                log.warn("Catalog id is not preload!");
            }
        } else {
            Optional<Catalog> last = repository.findFirstByCatalogParentIsNullOrderByIdDesc();
            if (last.isPresent()) {
                nextId = last.get().getId() + 1;
            }
        }
        log.debug("NEXT catalog id: {}", nextId);
        catalog.setId(nextId); // Se asigna el id + 1

        findByIdAndCatalogParent(catalogDto.getStatus().getId(), CatalogKeys.ESTATUS_MACHINE);
        catalog.setStatus(catalogDto.getStatus().getId());


        if(catalogDto.getType() == null && catalogDto.getType().getId() == null) {
            catalog.setType(CatalogKeys.CAT_TYPES_EXT);
        } else {
            findByIdAndCatalogParent(catalogDto.getType().getId(), CatalogKeys.CAT_TYPES);
            catalog.setType(catalogDto.getType().getId());
        }

        catalog.setIsRequired( catalog.getIsRequired() == null ? false : catalog.getIsRequired() );
        catalog.setCreatedBy(getCurrentUserId());
    }

    private Catalog validationUpdate(CatalogUpdateDto catalogDto) throws CustomException {
        Catalog catalog = findEntityById(catalogDto.getId());

        if(catalogDto.getType() != null && catalogDto.getType().getId() != null) {
            findByIdAndCatalogParent(catalogDto.getType().getId(), CatalogKeys.CAT_TYPES);
            catalog.setType(catalogDto.getType().getId());
        }

        return catalog;
    }

    private List<CatalogDto> parseCatalogDto(List<Catalog> catalogs) {
        List<CatalogDto> catalogDtoList = new ArrayList<>();
        catalogs.forEach(catalog -> {
            try {
                catalogDtoList.add(parseCatalogDto(catalog));
            } catch (Exception e) {
                log.warn("Catalog with id {} not added to list, error parsing: {}", catalog.getId(), e.getMessage());
            }
        });
        return catalogDtoList;
    }

    private List<Catalog> getCatalogsList(List<Long> catalogsParentId) {
        List<Catalog> catalogs = new ArrayList<>();
        catalogsParentId.forEach( catalogParentId -> {
            try {
                catalogs.add(findEntityById(catalogParentId));
            } catch (CustomException e) {
                log.warn(e.getMessage());
            }
        } );
        return catalogs;
    }
}
