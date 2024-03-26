package com.mexico.sas.admin.api.service.impl;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.project.*;
import com.mexico.sas.admin.api.exception.BadRequestException;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.exception.NoContentException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.Application;
import com.mexico.sas.admin.api.model.Employee;
import com.mexico.sas.admin.api.model.Project;
import com.mexico.sas.admin.api.model.ProjectApplication;
import com.mexico.sas.admin.api.repository.ProjectApplicationRepository;
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
public class ProjectApplicationServiceImpl extends LogMovementUtils implements ProjectApplicationService {

    @Autowired
    private ProjectApplicationRepository repository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LogMovementService logMovementService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CatalogService catalogService;

    @Override
    public void save(ProjectApplicationDto projectApplicationDto) throws CustomException {
        ProjectApplication projectApplication = from_M_To_N(projectApplicationDto, ProjectApplication.class);
        validationSave(projectApplicationDto, projectApplication);
        try {
            log.debug("Application {} for project {} to save",
                    projectApplicationDto.getApplication(), projectApplicationDto.getProjectKey());
            log.debug("Entity ::: {}", projectApplication);
            repository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_INSERT,
                    I18nResolver.getMessage(I18nKeys.LOG_GENERAL_CREATION));
            projectApplicationDto.setId(projectApplication.getId());
            log.debug("Application for project created with id: {}", projectApplicationDto.getId());
            updateProjectAmount(projectApplication.getProject());
        } catch (Exception e) {
            String msgError = I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_CREATED,
                    projectApplicationDto.getApplication(), projectApplicationDto.getProjectKey());
            log.error(msgError, e.getMessage());
            throw new CustomException(msgError);
        }
    }

    @Override
    public void update(Long projectApplicationId, ProjectApplicationUpdateDto projectApplicationUpdateDto) throws CustomException {
        try {
            ProjectApplicationDto result = findByProjectAndApplication(projectApplicationUpdateDto.getProjectKey(), projectApplicationUpdateDto.getApplication());
            if( !result.getId().equals(projectApplicationId) ) {
                throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_DUPLICATED,
                        projectApplicationUpdateDto.getApplication(), projectApplicationUpdateDto.getProjectKey()), null);
            }
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        ProjectApplication projectApplication = findEntityByApplicationId(projectApplicationId);
        String message = ChangeBeanUtils.checkProjectApplication(projectApplication, projectApplicationUpdateDto, employeeService);
        if(!message.isEmpty()) {
            repository.save(projectApplication);
            save(ProjectApplication.class.getSimpleName(), projectApplication.getId(), CatalogKeys.LOG_DETAIL_UPDATE, message);
            log.debug("Application updated!");
            updateProjectAmount(projectApplication.getProject());
        }
    }

    @Override
    public void deleteLogic(Long id) throws CustomException {
        log.debug("Delete logic: {}", id);
        ProjectApplication projectApplication = findEntityByApplicationId(id);
        repository.deleteLogic(id, !projectApplication.getEliminate(), projectApplication.getEliminate());
        save(ProjectApplication.class.getSimpleName(), id,
                !projectApplication.getEliminate() ? CatalogKeys.LOG_DETAIL_DELETE_LOGIC : CatalogKeys.LOG_DETAIL_STATUS,
                I18nResolver.getMessage(!projectApplication.getEliminate() ? I18nKeys.LOG_GENERAL_DELETE : I18nKeys.LOG_GENERAL_REACTIVE));
        updateProjectAmount(projectApplication.getProject());
    }

    @Override
    public void delete(Long id) throws CustomException {
        findEntityByApplicationId(id);
        try{
            repository.deleteById(id);
            save(ProjectApplication.class.getSimpleName(), id, CatalogKeys.LOG_DETAIL_DELETE, "TODO");
        } catch (Exception e) {
            throw new CustomException(I18nResolver.getMessage(I18nKeys.CATALOG_NOT_DELETED, id));
        }
    }

    @Override
    public ProjectApplicationFindDto findByApplicationId(Long id) throws CustomException {
        return getProjectApplicationFindDto(findEntityByApplicationId(id));
    }

    @Override
    public ProjectApplication findEntityByApplicationId(Long id) throws CustomException {
        log.debug("Finding project application: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id)));
    }

    @Override
    public List<ProjectApplicationPaggeableDto> findByProjectKey(String projectKey) {
        List<ProjectApplication> projectApplications = repository.findByProjectOrderByIdAsc(new Project(projectKey));
        List<ProjectApplicationPaggeableDto> applications = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                applications.add(getProjectApplicationPaggeableDto(pa));
            } catch (CustomException e) {
                log.error("Impossible add project application {}, error: {}", pa.getId(), e.getMessage());
            }
        });
        try {
            applications.add(getTotal(projectApplications));
        } catch (CustomException e) {
            log.error("Impossible add project application total, error: {}", e.getMessage());
        }
        updateProjectAmount( new Project(projectKey) );
        return applications;
    }

    @Override
    public ProjectApplicationDto findByProjectKeyAndId(String projectKey, Long id) throws CustomException {
        log.debug("Finding project application with projectKey: {} and id: {}", projectKey, id);
        return parseFromEntity(repository.findByProjectAndId(new Project(projectKey), id)
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, id))));
    }

    @Override
    public ProjectApplicationDto findByProjectAndApplication(String projectKey, String application) throws CustomException {
        log.debug("Finding project application with projectKey: {} and application: {}", projectKey, application);
        return parseFromEntity(repository.findByProjectAndApplicationAndActiveIsTrueAndEliminateIsFalse(new Project(projectKey), new Application(application))
                .orElseThrow(() -> new NoContentException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_NOT_FOUNT, application))));
    }

    @Override
    public Page<ProjectApplicationPaggeableDto> findPendings(String type, String filter, Pageable pageable) throws CustomException {
        Date currentDate = stringToDate(dateToString(new Date(), GeneralKeys.FORMAT_DDMMYYYY, true), GeneralKeys.FORMAT_DDMMYYYY);
        Page<ProjectApplication> projectApplications = findPendingsDinamyc(type, filter, currentDate, pageable);
        List<ProjectApplicationPaggeableDto> projectApplicationPaggeableDtos = new ArrayList<>();
        projectApplications.forEach( pa -> {
            try {
                projectApplicationPaggeableDtos.add(getProjectApplicationPaggeableDto(pa));
            } catch (CustomException e) {
                log.error("Impossible add project application {}, error: {}", pa.getId(), e.getMessage());
            }
        });
        return new PageImpl<>(projectApplicationPaggeableDtos, pageable, projectApplications.getTotalElements());
    }

    private ProjectApplicationDto parseFromEntity(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationDto projectApplicationDto = from_M_To_N(projectApplication, ProjectApplicationDto.class);
        projectApplicationDto.setProjectKey(projectApplication.getProject().getKey());
        projectApplicationDto.setApplication(projectApplication.getApplication().getName());
        projectApplicationDto.setLeaderId(projectApplication.getLeader().getId());
        projectApplicationDto.setDeveloperId(projectApplication.getDeveloper().getId());
        projectApplicationDto.setStartDate(dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectApplicationDto;
    }

    private ProjectApplicationFindDto getProjectApplicationFindDto(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationFindDto projectApplicationFindDto = from_M_To_N(projectApplication, ProjectApplicationFindDto.class);
        projectApplicationFindDto.setProjectKey(projectApplication.getProject().getKey());
        projectApplicationFindDto.setApplication(projectApplication.getApplication().getName());
        projectApplicationFindDto.setLeader(buildFullname(projectApplication.getLeader()));
        projectApplicationFindDto.setDeveloper(buildFullname(projectApplication.getDeveloper()));
        projectApplicationFindDto.setAmount(formatCurrency(projectApplication.getAmount()));
        projectApplicationFindDto.setTax(formatCurrency(projectApplication.getTax()));
        projectApplicationFindDto.setTotal(formatCurrency(projectApplication.getTotal()));
        projectApplicationFindDto.setStartDate(dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationFindDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        return projectApplicationFindDto;
    }

    private ProjectApplicationPaggeableDto getProjectApplicationPaggeableDto(ProjectApplication projectApplication) throws CustomException {
        ProjectApplicationPaggeableDto projectApplicationPaggeableDto = from_M_To_N(projectApplication, ProjectApplicationPaggeableDto.class);
        projectApplicationPaggeableDto.setProjectKey(projectApplication.getProject().getKey());
        projectApplicationPaggeableDto.setApplication(projectApplication.getApplication().getName());
        projectApplicationPaggeableDto.setLeader(buildFullname(projectApplication.getLeader()));
        projectApplicationPaggeableDto.setDeveloper(buildFullname(projectApplication.getDeveloper()));
        projectApplicationPaggeableDto.setAmount(formatCurrency(projectApplication.getAmount()));
        projectApplicationPaggeableDto.setTax(formatCurrency(projectApplication.getTax()));
        projectApplicationPaggeableDto.setTotal(formatCurrency(projectApplication.getTotal()));
        projectApplicationPaggeableDto.setStartDate(dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationPaggeableDto.setDesignDate(dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationPaggeableDto.setDevelopmentDate(dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        projectApplicationPaggeableDto.setEndDate(dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true));
        if( projectApplication.getDesignStatus() != null ) {
            projectApplicationPaggeableDto.setDesignStatusDesc(catalogService.findById(projectApplication.getDesignStatus()).getValue());
        }
        if( projectApplication.getDevelopmentStatus() != null ) {
            projectApplicationPaggeableDto.setDevelopmentStatusDesc(catalogService.findById(projectApplication.getDevelopmentStatus()).getValue());
        }
        if( projectApplication.getEndStatus() != null ) {
            projectApplicationPaggeableDto.setEndStatusDesc(catalogService.findById(projectApplication.getEndStatus()).getValue());
        }
        return projectApplicationPaggeableDto;
    }

    private ProjectApplicationPaggeableDto getTotal(List<ProjectApplication> projectApplications) throws CustomException {
        BigDecimal totalAmount = projectApplications.stream().filter( pa -> pa.getAmount() != null ).map( pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTax = projectApplications.stream().filter( pa -> pa.getTax() != null ).map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalT = projectApplications.stream().filter( pa -> pa.getTotal() != null ).map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        ProjectApplicationPaggeableDto projectApplicationPaggeableDto = new ProjectApplicationPaggeableDto();
        projectApplicationPaggeableDto.setApplication(GeneralKeys.FOOTER_TOTAL);
        projectApplicationPaggeableDto.setAmount(formatCurrency(totalAmount));
        projectApplicationPaggeableDto.setTax(formatCurrency(totalTax));
        projectApplicationPaggeableDto.setTotal(formatCurrency(totalT));
        return projectApplicationPaggeableDto;
    }

    private void validationSave(ProjectApplicationDto projectApplicationDto, ProjectApplication projectApplication) throws CustomException {
        projectApplication.setStartDate(stringToDate(projectApplicationDto.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setDesignDate(stringToDate(projectApplicationDto.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setDevelopmentDate(stringToDate(projectApplicationDto.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY));
        projectApplication.setEndDate(stringToDate(projectApplicationDto.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY));
        try {
            findByProjectAndApplication(projectApplicationDto.getProjectKey(), projectApplicationDto.getApplication());
            throw new BadRequestException(I18nResolver.getMessage(I18nKeys.PROJECT_APPLICATION_DUPLICATED,
                    projectApplicationDto.getApplication(), projectApplicationDto.getProjectKey()), null);
        } catch (CustomException e) {
            if(e instanceof BadRequestException)
                throw e;
        }
        // TODO Validate application exist in application catalog
        projectApplication.setProject(new Project(projectApplicationDto.getProjectKey()));
        projectApplication.setApplication(new Application(projectApplicationDto.getApplication()));
        projectApplication.setLeader(employeeService.findEntityById(projectApplicationDto.getLeaderId()));
        projectApplication.setDeveloper(employeeService.findEntityById(projectApplicationDto.getDeveloperId()));
        projectApplication.setCreatedBy(getCurrentUser().getUserId());
        projectApplication.setDesignStatus(CatalogKeys.PROJ_APP_STATUS_PENDING);
        projectApplication.setDevelopmentStatus(CatalogKeys.PROJ_APP_STATUS_PENDING);
        projectApplication.setEndStatus(CatalogKeys.PROJ_APP_STATUS_PENDING);
    }

    private void updateProjectAmount(Project project) {
        log.debug(" :::::: UPDATE PROJECT AMOUNT {} :::::::", project.getKey());
        List<ProjectApplication> projectApplications = repository.findByProjectAndActiveIsTrueAndEliminateIsFalse(project);
        log.debug(" :::::: APPLICATIONS: {} :::::::", projectApplications.size());
        BigDecimal amount = projectApplications.stream()
                .filter( pa -> pa.getAmount() != null )
                .map( pa -> pa.getAmount() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = projectApplications.stream()
                .filter( pa -> pa.getTax() != null )
                .map( pa -> pa.getTax() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = projectApplications.stream()
                .filter( pa -> pa.getTotal() != null )
                .map( pa -> pa.getTotal() ).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.debug(" :::::: Update project totals, AMOUNT: {} :::::: ", amount);
        projectService.updateAmounts(project.getKey(), amount, tax, total);
    }

    private Page<ProjectApplication> findPendingsDinamyc(String type, String filter, Date date, Pageable pageable) {
        return repository.findAll( (Specification<ProjectApplication>) (root, query, criteriaBuilder) ->
                getPredicatePendingsDinamyc(type, filter, date, criteriaBuilder, root), pageable);
    }

    private Predicate getPredicatePendingsDinamyc(String type, String filter, Date date, CriteriaBuilder builder, Root<ProjectApplication> root) {
        List<Predicate> predicates = new ArrayList<>();

        Long roleId = getCurrentUser().getRoleId();
        Long employeeId = getCurrentUser().getEmployeeId();

        log.debug("Finding pedings of type {}, for user: {} with role: {} and employeeId: {}, currentDate: {}, filter: {}",
                type, getCurrentUser().getUserId(), roleId, employeeId, date, filter );

        // There is filter, add string filter
        if(!StringUtils.isEmpty(filter)) {
            String patternFilter = String.format(GeneralKeys.PATTERN_LIKE, filter.toLowerCase());
            Predicate pKey = builder.like(builder.lower(root.get(ProjectApplication.Fields.project).get(Project.Fields.key)), patternFilter);
            Predicate pAppName = builder.like(builder.lower(root.get(ProjectApplication.Fields.application).get(Application.Fields.name)), patternFilter);
            predicates.add(builder.or(pKey, pAppName));
        }

        // User is not admin, add employee filter
        if(!(roleId.equals(CatalogKeys.ROLE_ROOT) || roleId.equals(CatalogKeys.ROLE_JAIME) || roleId.equals(CatalogKeys.ROLE_SELENE))) {
            Predicate pLeader = builder.equal(root.get(ProjectApplication.Fields.leader).get(Employee.Fields.id), employeeId);
            Predicate pDeveloper = builder.equal(root.get(ProjectApplication.Fields.developer).get(Employee.Fields.id), employeeId);
            predicates.add(builder.or(pLeader, pDeveloper));
        }

        if( type.equals(GeneralKeys.PENDING_TYPE_DUE) ) {
            Predicate pDesignDate = builder.lessThan(root.get(ProjectApplication.Fields.designDate), date);
            Predicate pDesignStatus = builder.notEqual(root.get(ProjectApplication.Fields.designStatus), CatalogKeys.PROJ_APP_STATUS_COMPLETE);
            Predicate pDevelopmentDate = builder.lessThan(root.get(ProjectApplication.Fields.developmentDate), date);
            Predicate pDevelopmentStatus = builder.notEqual(root.get(ProjectApplication.Fields.developmentStatus), CatalogKeys.PROJ_APP_STATUS_COMPLETE);
            Predicate pEndDate = builder.lessThan(root.get(ProjectApplication.Fields.endDate), date);
            Predicate pEndStatus = builder.notEqual(root.get(ProjectApplication.Fields.endStatus), CatalogKeys.PROJ_APP_STATUS_COMPLETE);
            Predicate pDesign = builder.and(pDesignDate, pDesignStatus);
            Predicate pDevelopment = builder.and(pDevelopmentDate, pDevelopmentStatus);
            Predicate pEnd = builder.and(pEndDate, pEndStatus);
            predicates.add(builder.or(pDesign, pDevelopment, pEnd));
        } else if ( type.equals(GeneralKeys.PENDING_TYPE_CRT) ) {
            predicates.add(builder.lessThanOrEqualTo(root.get(ProjectApplication.Fields.startDate), date));
            Predicate pDesignDate = builder.greaterThanOrEqualTo(root.get(ProjectApplication.Fields.designDate), date);
            Predicate pDevelopmentDate = builder.greaterThanOrEqualTo(root.get(ProjectApplication.Fields.developmentDate), date);
            Predicate pEndDate = builder.greaterThanOrEqualTo(root.get(ProjectApplication.Fields.endDate), date);
            Predicate pCurrentDate = builder.and(pDesignDate, pDevelopmentDate, pEndDate);

//            Predicate pDevelopmentCurrentDate = builder.greaterThan(root.get(ProjectApplication.Fields.developmentDate), date);
//            Predicate pEndCurrentDate = builder.greaterThan(root.get(ProjectApplication.Fields.endDate), date);
//            Predicate pDesignStatus = builder.equal(root.get(ProjectApplication.Fields.designStatus), CatalogKeys.PROJ_APP_STATUS_COMPLETE);
//            Predicate pDesign = builder.and(pDesignDate, pDesignStatus, pDevelopmentCurrentDate, pEndCurrentDate);
//            Predicate pDevelopmentStatus = builder.equal(root.get(ProjectApplication.Fields.developmentStatus), CatalogKeys.PROJ_APP_STATUS_COMPLETE);
//            Predicate pDevelopment = builder.and(pDevelopmentDate, pDevelopmentStatus, pEndCurrentDate);
//            Predicate pEnd = builder.and(pEndDate, pDesignStatus, pDevelopmentStatus);
//            Predicate pCompleteDate = builder.or(pDesign, pDevelopment, pEnd);

            predicates.add(/*builder.or(*/pCurrentDate/*, pCompleteDate)*/);

        } else if ( type.equals(GeneralKeys.PEDNING_TYPE_NXT) ) {
            predicates.add(builder.greaterThan(root.get(ProjectApplication.Fields.startDate), date));
        } else {
            log.warn("Type {} not supported!", type);
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
