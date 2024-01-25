package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.repository.ProjectRepository;
import com.mexico.sas.admin.api.service.*;
import com.mexico.sas.admin.api.util.ChangeBeanUtils;
import com.mexico.sas.admin.api.util.LogMovementUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl extends LogMovementUtils implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogMovementService logMovementService;

    @Override
    public Page<ProjectPageableDto> findAll(String filter, Pageable pageable) {
        Long roleId = getCurrentUser().getRoleId();
        Long companyId = getCurrentUser().getCompanyId();
        log.debug("Finding all projects with pagination for employee of company {}", companyId);
        Page<Project> projects = roleId.equals(CatalogKeys.ROLE_ROOT) || roleId.equals(CatalogKeys.ROLE_JAIME) || roleId.equals(CatalogKeys.ROLE_SELENE) ?
                findByFilter(filter, null, null, null, null, null,
                        null, null, null, pageable)
                : findByFilter(filter, new Company(companyId), getCurrentUser().getUserId(), null,
                null, null, null, null, null, pageable);

//        long total = roleId.equals(CatalogKeys.ROLE_ROOT) ? repository.count()
//                : (roleId.equals(CatalogKeys.ROLE_ADMIN)
//                ? repository.countByCompany(new Company(companyId))
//                : repository.countByCompanyAndCreatedBy(new Company(companyId), getCurrentUser().getUserId()));

        List<ProjectPageableDto> projectsPageableDto = new ArrayList<>();

        projects.forEach( project -> {
            try {
                projectsPageableDto.add(parseProjectPagged(project));
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
        });
        return new PageImpl<>(projectsPageableDto, pageable, projects.getTotalElements());
    }

    @Override
    public ProjectFindDto findById(Long id) throws CustomException {
        Project project = findEntityById(id);
        ProjectFindDto projectFindDto = parseFromEntity(project);
        List<ProjectApplicationFindDto> applications = new ArrayList<>();
        return projectFindDto;
    }

    @Override
    public ProjectFindDto findByKey(String key) throws CustomException {
        return parseFromEntity(repository.findByKey(key)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_BYKEY_NOT_FOUND, key))));
    }

    @Override
    public Project findEntityById(Long id) throws CustomException {
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_NOT_FOUND, id)));
    }

    @Override
    public void save(ProjectDto projectDto) throws CustomException {
        log.debug(" ::: Request to save ::: {}", projectDto);
        Project project = from_M_To_N(projectDto, Project.class);
        project.setAmount(BigDecimal.ZERO);
        project.setTax(BigDecimal.ZERO);
        project.setTotal(BigDecimal.ZERO);
        validationSave(projectDto, project);
        try {
            log.debug("Project to save, key: {}", project.getKey());
            repository.save(project);
            projectDto.setId(project.getId());
            log.debug("Project created with id: {}", projectDto.getId());
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_NOT_CREATED, projectDto.getKey());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectId, ProjectUpdateDto projectUpdateDto) throws CustomException {
        log.debug("Update project {} with {}", projectId, projectUpdateDto);
        Project project = findEntityById(projectId);
        String message = ChangeBeanUtils.checkProyect(project, projectUpdateDto, companyService, employeeService);
        if(!message.isEmpty()) {
            repository.save(project);
            save(Project.class.getSimpleName(), project.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
        }
    }

    @Override
    public void updateAmounts(Long projectId, BigDecimal amount, BigDecimal tax, BigDecimal total) {
        repository.updateAmount(projectId, amount, tax, total);
    }

    @Override
    public void deleteLogic(Long id) throws CustomException {
        log.debug("Delete logic: {}", id);
        Project project = findEntityById(id);
        repository.deleteLogic(id, !project.getEliminate(), project.getEliminate());
        save(Project.class.getSimpleName(), id,
                !project.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
                I18nResolver.getMessage(!project.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityById(id);
        try{
            repository.deleteById(id);
            save(Project.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
        }
    }

    private ProjectFindDto parseFromEntity(Project project) throws CustomException {
        ProjectFindDto projectFindDto = from_M_To_N(project, ProjectFindDto.class);
        projectFindDto.setCreatedBy(logMovementService
                .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
        projectFindDto.setCompanyId(project.getCompany().getId());
        projectFindDto.setProjectManagerId(project.getProjectManager().getId());
        projectFindDto.setCreatedBy(buildFullname(employeeService.findEntityById(project.getCreatedBy())));
        projectFindDto.setCreationDate(dateToString(project.getCreationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectFindDto.setInstallationDate(dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectFindDto.setAmountStr(formatCurrency(project.getAmount().doubleValue()));
        projectFindDto.setTaxStr(formatCurrency(project.getTax().doubleValue()));
        projectFindDto.setTotalStr(formatCurrency(project.getTotal().doubleValue()));
        return projectFindDto;
    }

    private ProjectPageableDto parseProjectPagged(Project project) throws CustomException {
        ProjectPageableDto projectPageableDto = from_M_To_N(project, ProjectPageableDto.class);
        projectPageableDto.setCreatedBy(logMovementService
                .findFirstMovement(Project.class.getSimpleName(), project.getId()).getUserName());
        projectPageableDto.setCompany(project.getCompany().getName());
        projectPageableDto.setProjectManager(buildFullname(project.getProjectManager()));
        projectPageableDto.setCreatedBy(buildFullname(employeeService.findEntityById(project.getCreatedBy())));
        projectPageableDto.setCreationDate(dateToString(project.getCreationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectPageableDto.setInstallationDate(dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectPageableDto.setAmount(formatCurrency(project.getAmount().doubleValue()));
        projectPageableDto.setTax(formatCurrency(project.getTax().doubleValue()));
        projectPageableDto.setTotal(formatCurrency(project.getTotal().doubleValue()));
        return projectPageableDto;
    }

    private void validationSave(ProjectDto projectDto, Project project) throws CustomException {
        project.setInstallationDate(stringToDate(projectDto.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByKey(project.getKey());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_KEY_DUPLICATED, projectDto.getKey()), null);
        } catch (CustomException e) {
            if ( e instanceof BadRequestException)
                throw e;
        }
        project.setCompany(new Company(getCurrentUser().getCompanyId()));
        project.setProjectManager(employeeService.findEntityById(projectDto.getProjectManagerId()));
        project.setCreatedBy(getCurrentUser().getUserId());
    }

    private Page<Project> findByFilter(String filter, Company company, Long createdBy,
                                       Long status, Long projectManagerId, Boolean active,
                                       Date installationDate, Date startDate, Date endDate,
                                       Pageable pageable) {
        return repository.findAll((Specification<Project>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, company, createdBy, status, projectManagerId, active,
                        installationDate, startDate, endDate, criteriaBuilder, root), pageable);
    }

    private Long countByFilter(String filter, Company company, Long createdBy,
                                       Long status, Long projectManagerId, Boolean active,
                                       Date installationDate, Date startDate, Date endDate) {
        return repository.count((Specification<Project>) (root, query, criteriaBuilder) ->
                getPredicateDinamycFilter(filter, company, createdBy, status, projectManagerId, active,
                        installationDate, startDate, endDate, criteriaBuilder, root));
    }

    /**
     *
     * @param filter
     * @param company
     * @param createdBy
     * @param status
     * @param projectManagerId
     * @param active
     * @param startDate
     * @param endDate
     * @param builder
     * @param root
     * @return
     */
    private Predicate getPredicateDinamycFilter(String filter, Company company, Long createdBy,
                                                Long status, Long projectManagerId, Boolean active,
                                                Date installationDate, Date startDate, Date endDate,
                                                CriteriaBuilder builder, Root<Project> root) {
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(builder.isFalse(root.get(Project.Fields.eliminate)));

        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pKey = builder.like(builder.lower(root.get(Project.Fields.key)), patternFilter);
            Predicate pDescription = builder.like(builder.function(GeneralKeys.PG_FUNCTION_ACCENT, String.class, builder.lower(root.get(Project.Fields.description))), patternFilter);
            predicates.add(builder.or(pKey, pDescription));
        }

        if(company != null) {
            predicates.add(builder.equal(root.get(Project.Fields.company), company));
        }

        if(createdBy != null) {
            predicates.add(builder.equal(root.get(Project.Fields.createdBy), createdBy));
        }

        if(status != null) {
            predicates.add(builder.equal(root.get(Project.Fields.status), status));
        }

        if(projectManagerId != null) {
            predicates.add(builder.equal(root.get(Project.Fields.projectManager), projectManagerId));
        }

        if(active != null) {
            predicates.add(builder.equal(root.get(Project.Fields.active), active));
        }

        if(installationDate != null) {
            predicates.add(builder.equal(root.get(Project.Fields.installationDate), installationDate));
        }

        if(startDate != null && endDate != null) {
            predicates.add(builder.between(root.get(Project.Fields.creationDate), startDate, endDate));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
