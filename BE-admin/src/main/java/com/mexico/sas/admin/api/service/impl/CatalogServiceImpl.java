package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.dto.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.exception.ValidationRequestException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Catalog;
import com.mexico.sas.admin.api.model.CatalogModule;
import com.mexico.sas.admin.api.repository.CatalogModuleRepository;
import com.mexico.sas.admin.api.repository.CatalogRepository;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oziel Naranjo
 */
@Slf4j
@Service
public class CatalogServiceImpl extends Utils implements CatalogService {

    @Autowired
    private CatalogRepository repository;

    @Autowired
    private CatalogModuleRepository catalogModuleRepository;

    @Override
    public CatalogDto save(CatalogDto catalogDto) throws CustomException {
        log.debug("Saving catalog {} ...", catalogDto);
        Catalog catalog = from_M_To_N(catalogDto, Catalog.class);

        validationSave(catalogDto, catalog);

        catalog.setUserId(getCurrentUserId());
        catalog.setCreationDate(new Date());

        try{
            repository.save(catalog);
            save(Catalog.class.getSimpleName(), catalog.getId(), null, catalog.getUserId(), catalog.getCreationDate(),
                    CatalogKeys.LOG_EVENT_INSERT, CatalogKeys.LOG_DETAIL_INSERT);
            saveModels(catalog.getId(), catalog.getUserId(), catalogDto.getModules());
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
            Long userId = getCurrentUserId();
            save(Catalog.class.getSimpleName(), catalog.getId(), null, userId, new Date(),
                    CatalogKeys.LOG_EVENT_UPDATE, CatalogKeys.LOG_DETAIL_UPDATE);
            saveModels(catalog.getId(), userId, catalogDto.getModules());
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
        save(Catalog.class.getSimpleName(), updateStatusDto.getId(), null,
                getCurrentUserId(), new Date(),
                updateStatusDto.getStatusId().equals(CatalogKeys.ESTATUS_MACHINE_ELIMINATED) ?
                        CatalogKeys.LOG_EVENT_DELETE : CatalogKeys.LOG_EVENT_UPDATE,
                updateStatusDto.getStatusId().equals(CatalogKeys.ESTATUS_MACHINE_ELIMINATED) ?
                        CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS);
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityById(id);
        try{
            repository.deleteById(id);
            save(Catalog.class.getSimpleName(), id, null, getCurrentUserId(), new Date(),
                    CatalogKeys.LOG_EVENT_DELETE, CatalogKeys.LOG_DETAIL_DELETE);
        } catch (Exception e) {
            handleCatalogIdNotFound(id, I18nKeys.CATALOG_NOT_DELETED);
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

    @Override
    public List<CatalogSimpleDto> getModulesDto(Long catalogId) {
        List<CatalogSimpleDto> catalogSimpleDtos = new ArrayList<>();
        List<CatalogModule> catalogModules = catalogModuleRepository.findByCatalogId(catalogId);
        if(catalogModules.isEmpty()) {
            return catalogSimpleDtos;
        }
        catalogModules.forEach(catalogModule -> {
            try {
                Catalog module = findEntityById(catalogModule.getCatalogModuleId());
                CatalogSimpleDto catalogSimpleDto = from_M_To_N(module, CatalogSimpleDto.class);
                catalogSimpleDto.setDescription(module.getValue());
                catalogSimpleDtos.add(catalogSimpleDto);
            } catch (CustomException e) {
                log.warn("Impossible parsing catalog, error: {}", e.getMessage());
            }
        });
        return catalogSimpleDtos;
    }

    @Override
    public CatalogModule findEntityCatalogModule(Long id) throws CustomException {
        return catalogModuleRepository.findById(id).orElseThrow(() ->
                new NoContentException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, id)));
    }

    private CatalogDto parseCatalogDto(Catalog catalog) throws CustomException {
        CatalogDto catalogDto = from_M_To_N(catalog, CatalogDto.class);
        catalogDto.setLastUpdate(getLastMovementDate(Catalog.class.getSimpleName(), catalog.getId(), null));
        if(catalog.getCatalogParent() != null && catalog.getCatalogParent().getId() != null)
            catalogDto.setCatalogParent(catalog.getCatalogParent().getId());
        if(catalog.getStatus()!=null) {
            catalogDto.setStatus(getCatalogSimpleDto(catalog.getStatus()));
        }
        if(catalog.getType()!=null) {
            catalogDto.setType(getCatalogSimpleDto(catalog.getType()));
        }
        catalogDto.setModules(getModulesDto(catalog.getId()));
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
        catalogDto.setModules(getModulesDto(catalog.getId()));
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

    private void validationSave(CatalogDto catalogDto, Catalog catalog) throws ValidationRequestException {
        List<ResponseErrorDetailDto> errors = new ArrayList<>();
        try {
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
        } catch (CustomException e) {
            errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.catalogParent, e.getMessage()));
        }

        try {
            findByIdAndCatalogParent(catalogDto.getStatus().getId(), CatalogKeys.ESTATUS_MACHINE);
            catalog.setStatus(catalogDto.getStatus().getId());
        } catch (CustomException e) {
            errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.status, e.getMessage()));
        }

        if(catalogDto.getType() == null && catalogDto.getType().getId() == null) {
            catalog.setType(CatalogKeys.CAT_TYPES_EXT);
        } else {
            try {
                findByIdAndCatalogParent(catalogDto.getType().getId(), CatalogKeys.CAT_TYPES);
                catalog.setType(catalogDto.getType().getId());
            } catch (CustomException e) {
                errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.type, e.getMessage()));
            }
        }

        if(catalogDto.getModules()!= null && !catalogDto.getModules().isEmpty()) {
            log.debug("Validing modules ...");
            catalogDto.getModules().forEach(module -> {
                try {
                    findByIdAndCatalogParent(module.getId(), CatalogKeys.CAT_MODULES);

                } catch (CustomException e) {
                    errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.modules, e.getMessage()));
                }
            });
        }

        if(!errors.isEmpty()) {
            log.debug("Errors: {}", errors);
            throw new ValidationRequestException(errors);
        }
        catalog.setIsRequired( catalog.getIsRequired() == null ? false : catalog.getIsRequired() );
    }

    private Catalog validationUpdate(CatalogUpdateDto catalogDto) throws ValidationRequestException {
        Catalog catalog = null;
        List<ResponseErrorDetailDto> errors = new ArrayList<>();
        try {
            catalog = findEntityById(catalogDto.getId());
        } catch (CustomException e) {
            errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.id,
                    I18nResolver.getMessage(I18nKeys.CATALOG_NOT_FOUND, catalogDto.getId())));
            throw new ValidationRequestException(errors);
        }

        if(catalogDto.getType() != null && catalogDto.getType().getId() != null) {
            try {
                findByIdAndCatalogParent(catalogDto.getType().getId(), CatalogKeys.CAT_TYPES);
                catalog.setType(catalogDto.getType().getId());
            } catch (CustomException e) {
                errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.type, e.getMessage()));
            }
        }

        if(catalogDto.getModules()!= null && !catalogDto.getModules().isEmpty()) {
            catalogDto.getModules().forEach(module -> {
                try {
                    findByIdAndCatalogParent(module.getId(), CatalogKeys.CAT_MODULES);
                } catch (CustomException e) {
                    errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.modules, e.getMessage()));
                }
            });
        }

        if(!errors.isEmpty()) {
            throw new ValidationRequestException(errors);
        }
        return catalog;
    }

    private void handleCatalogIdNotFound(Long id, String property) throws ValidationRequestException {
        List<ResponseErrorDetailDto> errors = new ArrayList<>();
        errors.add(new ResponseErrorDetailDto(CatalogDto.Fields.id, I18nResolver.getMessage(property, id)));
        throw new ValidationRequestException(errors);
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

    private void saveModels(Long catalogId, Long userId, List<CatalogSimpleDto> modules) {
        if(catalogId != null && modules != null && !modules.isEmpty()) {
            catalogModuleRepository.deleteByCatalogId(catalogId);
        }
        if(modules != null && !modules.isEmpty()) {
            Date creationDate = new Date();
            List<CatalogModule> catalogModules = new ArrayList<>();
            modules.forEach(module -> {
                CatalogModule catalogModule = new CatalogModule();
                catalogModule.setCatalogId(catalogId);
                catalogModule.setCatalogModuleId(module.getId());
                catalogModule.setUserId(userId);
                catalogModule.setCreationDate(creationDate);
                catalogModules.add(catalogModule);
            });
            catalogModuleRepository.saveAll(catalogModules);
            saveAll(CatalogModule.class.getSimpleName(), catalogModules.stream().map(CatalogModule::getId).collect(Collectors.toList()),
                    userId, creationDate);
        }
    }
}
