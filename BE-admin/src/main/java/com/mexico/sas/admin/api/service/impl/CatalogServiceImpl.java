package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.catalog.CatalogDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogPaggedDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Catalog;
import com.mexico.sas.admin.api.repository.CatalogRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
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
    @Autowired
    private CompanyService companyService;

    @Override
    public CatalogDto save(CatalogDto catalogDto) throws CustomException {
        log.debug("Saving catalog {} ...", catalogDto);
        Catalog catalog = from_M_To_N(catalogDto, Catalog.class);

        validationSave(catalogDto, catalog);

        try{
            repository.save(catalog);
            save(Catalog.class.getSimpleName(),
                    catalogDto.getCatalogParent() == null ? catalog.getId() : catalogDto.getCatalogParent(),
                    CatalogKeys.LOG_DETAIL_INSERT, I18nResolver.getMessage(I18nKeys.LOG_CATALOG_CREATION, catalogDto.getValue()));
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
        String message = ChangeBeanUtils.checkCatalog(catalog, catalogDto);
        if(!message.isEmpty()) {
            try {
                repository.save(catalog);
                save(Catalog.class.getSimpleName(),
                        catalog.getCatalogParent() == null ? catalog.getId() : catalog.getCatalogParent().getId(),
                        CatalogKeys.LOG_DETAIL_UPDATE, message);
            } catch (Exception e) {
                throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_UPDATED, catalog.getId()));
            }
        }
        return findById(id);
    }

    @Override
    public void deleteLogic(Long id) {
        log.debug("Delete logic: {}", id);
        repository.updateStatus(id, CatalogKeys.ESTATUS_MACHINE_ELIMINATED);
        save(Catalog.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE_LOGIC,
                I18nResolver.getMessage(I18nKeys.LOG_GENERAL_DELETE));
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
    public List<CatalogPaggedDto> findAll() throws CustomException {
        List<Catalog> catalogs = getCurrentUser().getRoleId().equals(CatalogKeys.ROLE_ROOT)
                ? repository.findByCatalogParentIsNullAndIdNotInOrderByIdAsc(catalogsNotIn())
                : repository.findByCatalogParentIsNullAndStatusIsNotAndInternalIsFalseAndIdNotInOrderByIdAsc(
                        CatalogKeys.ESTATUS_MACHINE_ELIMINATED, catalogsNotIn());
        return parsingPage(catalogs);
    }

    @Override
    public List<CatalogPaggedDto> findChildsByParentId(Long id) {
        List<Catalog> catalogs = getCurrentUser().getCompanyId().equals(CatalogKeys.COMPANY_SAS)
                ? repository.findByCatalogParentOrderByIdAsc(new Catalog(id))
                : repository.findByCatalogParentAndCompanyIdAndStatusIsNotOrderByIdAsc(
                        new Catalog(id), getCurrentUser().getCompanyId(), CatalogKeys.ESTATUS_MACHINE_ELIMINATED);
        return parsingPage(catalogs);
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

    private CatalogDto parseCatalogDto(Catalog catalog) throws CustomException {
        CatalogDto catalogDto = from_M_To_N(catalog, CatalogDto.class);
        if(catalog.getCatalogParent() != null && catalog.getCatalogParent().getId() != null)
            catalogDto.setCatalogParent(catalog.getCatalogParent().getId());
        return catalogDto;
    }

    private CatalogPaggedDto parseCatalogPaggedDto(Catalog catalog) throws CustomException {
        CatalogPaggedDto catalogDto = from_M_To_N(catalog, CatalogPaggedDto.class);
        catalogDto.setCompany(companyService.findById(catalog.getCompanyId()).getName());
        catalogDto.setStatusDesc(findEntityById(catalog.getStatus()).getValue());
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
        catalog.setCompanyId(getCurrentUser().getCompanyId());
        catalog.setCreatedBy(getCurrentUser().getUserId());
    }

    private Catalog validationUpdate(CatalogUpdateDto catalogDto) throws CustomException {
        Catalog catalog = findEntityById(catalogDto.getId());

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
