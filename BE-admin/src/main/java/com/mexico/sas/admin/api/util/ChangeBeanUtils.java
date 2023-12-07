package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationUpdateDto;
import com.mexico.sas.admin.api.dto.project.ProjectUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangeBeanUtils extends Utils {

    public static String checkCatalog(Catalog catalog, CatalogUpdateDto catalogUpdateDto) {
        StringBuilder sb = new StringBuilder();

        if(catalogUpdateDto.getValue() != null && !catalogUpdateDto.getValue().equalsIgnoreCase(catalog.getValue())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, CatalogUpdateDto.Fields.value,
                    catalog.getValue(), catalogUpdateDto.getValue())).append(GeneralKeys.JUMP_LINE);
            catalog.setValue(catalogUpdateDto.getValue());
        } if(catalogUpdateDto.getDescription() != null && !catalogUpdateDto.getDescription().equalsIgnoreCase(catalog.getDescription())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), CatalogUpdateDto.Fields.description,
                    catalog.getDescription(), catalogUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            catalog.setDescription(catalogUpdateDto.getDescription());
        } if(catalogUpdateDto.getStatus() != null && !catalogUpdateDto.getStatus().equals(catalog.getStatus())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), CatalogUpdateDto.Fields.status,
                    catalog.getStatus(), catalogUpdateDto.getStatus())).append(GeneralKeys.JUMP_LINE);
            catalog.setStatus(catalogUpdateDto.getStatus());
        }

        return sb.toString().trim();
    }

    public static String checkProyect(Project project, ProjectUpdateDto projectUpdateDto) {
        StringBuilder sb = new StringBuilder();

        if( !project.getDescription().equals(projectUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.description,
                    project.getDescription(), projectUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            project.setDescription(projectUpdateDto.getDescription());
        }
        if( projectUpdateDto.getCompanyId() != null && !project.getCompany().getId().equals(projectUpdateDto.getCompanyId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.companyId,
                    project.getCompany().getId(), projectUpdateDto.getCompanyId())).append(GeneralKeys.JUMP_LINE);
            project.setCompany(new Company(projectUpdateDto.getCompanyId()));
        }
        if( !project.getProjectManager().getId().equals(projectUpdateDto.getProjectManagerId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.projectManagerId,
                    project.getProjectManager().getId(), projectUpdateDto.getProjectManagerId())).append(GeneralKeys.JUMP_LINE);
            project.setProjectManager(new Employee(projectUpdateDto.getProjectManagerId()));
        }
        try {
            String currentDate = dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if ((project.getInstallationDate() == null && projectUpdateDto.getInstallationDate() != null) || (!currentDate.equals(projectUpdateDto.getInstallationDate()))) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.installationDate,
                        currentDate, projectUpdateDto.getInstallationDate())).append(GeneralKeys.JUMP_LINE);
                project.setInstallationDate(stringToDate(projectUpdateDto.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking project installation dates, error: {}", e.getMessage());
        }

        return sb.toString().trim();
    }

    public static String checkProjectApplication(ProjectApplication projectApplication, ProjectApplicationUpdateDto projectApplicationUpdateDto) {
        StringBuilder sb = new StringBuilder();

        if( !projectApplication.getApplicationId().equals(projectApplicationUpdateDto.getApplicationId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.applicationId,
                    projectApplication.getApplicationId(), projectApplicationUpdateDto.getApplicationId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setApplicationId(projectApplicationUpdateDto.getApplicationId());
        }
        double currentAmount = doubleScale(projectApplication.getAmount().doubleValue());
        double newAmount = doubleScale(projectApplicationUpdateDto.getAmount().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.amount,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            projectApplication.setAmount(projectApplicationUpdateDto.getAmount());
        }
        if( !projectApplication.getLeader().getId().equals(projectApplicationUpdateDto.getLeaderId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.leaderId,
                    projectApplication.getLeader().getId(), projectApplicationUpdateDto.getLeaderId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setLeader(new Employee(projectApplicationUpdateDto.getLeaderId()));
        }
        if( !projectApplication.getDeveloper().getId().equals(projectApplicationUpdateDto.getDeveloperId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.developerId,
                    projectApplication.getDeveloper().getId(), projectApplicationUpdateDto.getDeveloperId())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDeveloper(new Employee(projectApplicationUpdateDto.getDeveloperId()));
        }
        String currentDate = null;
        try {
            currentDate = dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( !currentDate.equals(projectApplicationUpdateDto.getStartDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.startDate,
                        currentDate, projectApplicationUpdateDto.getStartDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setStartDate(stringToDate(projectApplicationUpdateDto.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking start date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( !currentDate.equals(projectApplicationUpdateDto.getDesignDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.designDate,
                        currentDate, projectApplicationUpdateDto.getDesignDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setDesignDate(stringToDate(projectApplicationUpdateDto.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking design date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( !currentDate.equals(projectApplicationUpdateDto.getDevelopmentDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.developmentDate,
                        currentDate, projectApplicationUpdateDto.getDevelopmentDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setDevelopmentDate(stringToDate(projectApplicationUpdateDto.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking development date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( !currentDate.equals(projectApplicationUpdateDto.getEndDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectApplicationUpdateDto.Fields.endDate,
                        currentDate, projectApplicationUpdateDto.getEndDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setEndDate(stringToDate(projectApplicationUpdateDto.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking end date, error: {}", e.getMessage());
        }

        return sb.toString().trim();
    }
}
