package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.dto.company.CompanyUpdateDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeUpdateDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationUpdateDto;
import com.mexico.sas.admin.api.dto.project.ProjectUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangeBeanUtils extends Utils {

    private static boolean validateStringRequiredUpdate(String s1, String s2) {
        return s2 != null && !s2.isEmpty() && !s1.equalsIgnoreCase(s2) ;
    }
    private static boolean validateStringNoRequiredUpdate(String s1, String s2) {
        return ( s1 == null && s2 != null && !s2.isEmpty() ) || ( s1 != null && s2 != null && !s1.equalsIgnoreCase(s2) );
    }

    private static boolean validateLongRequiredUpdate(Long l1, Long l2) {
        return l2 != null && !l1.equals(l2) ;
    }

    private static boolean validateLongNoRequiredUpdate(Long l1, Long l2) {
        return (l1 == null && l2 != null) || (l1 != null && l2 != null && !l1.equals(l2)) ;
    }

    private static boolean validateBooleanNoRequiredUpdate(Boolean b1, Boolean b2) {
        return (b1 == null && b2 != null) || (b1 != null && b2 != null && !b1.equals(b2)) ;
    }

    public static String checkCatalog(Catalog catalog, CatalogUpdateDto catalogUpdateDto) {
        StringBuilder sb = new StringBuilder();

        if( validateStringRequiredUpdate(catalog.getValue(), catalogUpdateDto.getValue()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CatalogUpdateDto.Fields.value,
                    catalog.getValue(), catalogUpdateDto.getValue())).append(GeneralKeys.JUMP_LINE);
            catalog.setValue(catalogUpdateDto.getValue());
        }
        if( validateStringRequiredUpdate(catalog.getDescription(), catalogUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), CatalogUpdateDto.Fields.description,
                    catalog.getDescription(), catalogUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            catalog.setDescription(catalogUpdateDto.getDescription());
        }
        if( validateLongNoRequiredUpdate(catalog.getStatus(), catalogUpdateDto.getStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), CatalogUpdateDto.Fields.status,
                    catalog.getStatus(), catalogUpdateDto.getStatus())).append(GeneralKeys.JUMP_LINE);
            catalog.setStatus(catalogUpdateDto.getStatus());
        }

        return sb.toString().trim();
    }

    public static String checkProyect(Project project, ProjectUpdateDto projectUpdateDto) {
        StringBuilder sb = new StringBuilder();

        if( !project.getKey().equals(projectUpdateDto.getKey()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.key,
                    project.getKey(), projectUpdateDto.getKey())).append(GeneralKeys.JUMP_LINE);
            project.setKey(projectUpdateDto.getKey());
        }
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
        double currentAmount = doubleScale(project.getAmount().doubleValue());
        double newAmount = doubleScale(projectUpdateDto.getAmount().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, ProjectUpdateDto.Fields.amount,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            project.setAmount(projectUpdateDto.getAmount());
            project.setTax(projectUpdateDto.getTax());
            project.setTotal(projectUpdateDto.getTotal());
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
        String currentDate = null;
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
            projectApplication.setTax(projectApplicationUpdateDto.getTax());
            projectApplication.setTotal(projectApplicationUpdateDto.getTotal());
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

    public static String checkOrder(Order order, OrderDto orderDto) {
        StringBuilder sb = new StringBuilder();
        String currentDate = null;
        if( (order.getStatus() == null && orderDto.getStatus() != null)
                || ( order.getStatus() != null && orderDto.getStatus() != null && !order.getStatus().equals(orderDto.getStatus())) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.status,
                    order.getStatus(), orderDto.getStatus())).append(GeneralKeys.JUMP_LINE);
            order.setStatus(orderDto.getStatus());
        }
        double currentAmount = doubleScale(order.getAmount().doubleValue());
        double newAmount = doubleScale(orderDto.getAmount().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.amount,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            order.setAmount(orderDto.getAmount());
            order.setTax(orderDto.getTax());
            order.setTotal(orderDto.getTotal());
        }
        try {
            currentDate = dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( currentDate == null || !currentDate.equals(orderDto.getOrderDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.orderDate,
                        currentDate, orderDto.getOrderDate())).append(GeneralKeys.JUMP_LINE);
                order.setOrderDate(stringToDate(orderDto.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking order date, error: {}", e.getMessage());
        }

        if( !order.getRequisition().equals(orderDto.getRequisition())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.requisition,
                    order.getRequisition(), orderDto.getRequisition())).append(GeneralKeys.JUMP_LINE);
            order.setRequisition(orderDto.getRequisition());
        }
        try {
            currentDate = dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( !currentDate.equals(orderDto.getRequisitionDate())) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.requisitionDate,
                        currentDate, orderDto.getRequisitionDate())).append(GeneralKeys.JUMP_LINE);
                order.setRequisitionDate(stringToDate(orderDto.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking end date, error: {}", e.getMessage());
        }
        if( (order.getRequisitionStatus() == null && orderDto.getRequisitionStatus() != null)
                || ( order.getRequisitionStatus() != null && orderDto.getRequisitionStatus() != null && !order.getRequisitionStatus().equals(orderDto.getRequisitionStatus())) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, OrderDto.Fields.requisitionStatus,
                    order.getRequisitionStatus(), orderDto.getRequisitionStatus())).append(GeneralKeys.JUMP_LINE);
            order.setRequisitionStatus(orderDto.getRequisitionStatus());
        }

        return sb.toString().trim();
    }

    public static String checkInvoice(Invoice invoice, InvoiceDto invoiceDto) {
        StringBuilder sb = new StringBuilder();
        String currentDate = null;
        if( (invoice.getStatus() == null && invoiceDto.getStatus() != null)
                || ( invoice.getStatus() != null && invoiceDto.getStatus() != null && !invoice.getStatus().equals(invoiceDto.getStatus()) ) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.status,
                    invoice.getStatus(), invoiceDto.getStatus())).append(GeneralKeys.JUMP_LINE);
            invoice.setStatus(invoiceDto.getStatus());
        }
        double currentAmount = doubleScale(invoice.getAmount().doubleValue());
        double newAmount = doubleScale(invoiceDto.getAmount().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.amount,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            invoice.setAmount(invoiceDto.getAmount());
        }
        currentAmount = doubleScale(invoice.getTax().doubleValue());
        newAmount = doubleScale(invoiceDto.getTax().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.tax,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            invoice.setTax(invoiceDto.getTax());
        }
        currentAmount = doubleScale(invoice.getTotal().doubleValue());
        newAmount = doubleScale(invoiceDto.getTotal().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.total,
                    currentAmount, newAmount)).append(GeneralKeys.JUMP_LINE);
            invoice.setTotal(invoiceDto.getTotal());
        }
        try {
            currentDate = dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( currentDate == null || !currentDate.equals(invoiceDto.getIssuedDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.issuedDate,
                        currentDate, invoiceDto.getIssuedDate())).append(GeneralKeys.JUMP_LINE);
                invoice.setIssuedDate(stringToDate(invoiceDto.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking issued date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( currentDate == null || !currentDate.equals(invoiceDto.getPaymentDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, InvoiceDto.Fields.paymentDate,
                        currentDate, invoiceDto.getPaymentDate())).append(GeneralKeys.JUMP_LINE);
                invoice.setPaymentDate(stringToDate(invoiceDto.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking payment date, error: {}", e.getMessage());
        }
        return sb.toString().trim();
    }

    public static String checkEmployee(Employee employee, EmployeeUpdateDto employeeUpdateDto) {
        StringBuilder sb = new StringBuilder();
        if(employeeUpdateDto.getEmail() != null && !employeeUpdateDto.getEmail().equalsIgnoreCase(employee.getEmail())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.email,
                    employee.getEmail(), employeeUpdateDto.getEmail())).append(GeneralKeys.JUMP_LINE);
            employee.setEmail(employeeUpdateDto.getEmail());
        }
        if(employeeUpdateDto.getName() != null && !employeeUpdateDto.getName().equalsIgnoreCase(employee.getName())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.name,
                    employee.getName(), employeeUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            employee.setName(employeeUpdateDto.getName());
        }
        if( validateStringNoRequiredUpdate(employee.getSecondName(), employeeUpdateDto.getSecondName()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.secondName,
                    employee.getSecondName(), employeeUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            employee.setSecondName(employeeUpdateDto.getSecondName());
        }
        if(employeeUpdateDto.getSurname() != null && !employeeUpdateDto.getSurname().equalsIgnoreCase(employee.getSurname())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.surname,
                    employee.getSurname(), employeeUpdateDto.getSurname())).append(GeneralKeys.JUMP_LINE);
            employee.setSurname(employeeUpdateDto.getSurname());
        }
        if( validateStringNoRequiredUpdate(employee.getSecondSurname(), employeeUpdateDto.getSecondSurname()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.secondSurname,
                    employee.getSecondSurname(), employeeUpdateDto.getSecondSurname())).append(GeneralKeys.JUMP_LINE);
            employee.setSecondSurname(employeeUpdateDto.getSecondSurname());
        }
        if((employee.getCompanyId() == null && employeeUpdateDto.getCompanyId() != null)
                || (employeeUpdateDto.getCompanyId() != null && !employeeUpdateDto.getCompanyId().equals(employee.getCompanyId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.companyId,
                    employee.getCompanyId(), employeeUpdateDto.getCompanyId())).append(GeneralKeys.JUMP_LINE);
            employee.setCompanyId(employeeUpdateDto.getCompanyId());
        }
        if((employee.getPositionId() == null && employeeUpdateDto.getPositionId() != null)
                || (employee.getPositionId() != null && employeeUpdateDto.getPositionId() != null && !employeeUpdateDto.getPositionId().equals(employee.getPositionId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.positionId,
                    employee.getPositionId(), employeeUpdateDto.getPositionId())).append(GeneralKeys.JUMP_LINE);
            employee.setPositionId(employeeUpdateDto.getPositionId());
        }
        if( validateStringNoRequiredUpdate(employee.getPhone(), employeeUpdateDto.getPhone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.phone,
                    employee.getPhone(), employeeUpdateDto.getPhone())).append(GeneralKeys.JUMP_LINE);
            employee.setPhone(employeeUpdateDto.getPhone());
        }
        if((employee.getBossId() == null && employeeUpdateDto.getBossId() != null)
                || (employee.getBossId() != null && employeeUpdateDto.getBossId() != null && !employeeUpdateDto.getBossId().equals(employee.getBossId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.bossId,
                    employee.getBossId(), employeeUpdateDto.getBossId())).append(GeneralKeys.JUMP_LINE);
            employee.setBossId(employeeUpdateDto.getBossId());
        }
        if(employeeUpdateDto.getActive() != null && !employeeUpdateDto.getActive().equals(employee.getActive())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.active,
                    employee.getActive(), employeeUpdateDto.getActive())).append(GeneralKeys.JUMP_LINE);
            employee.setActive(employeeUpdateDto.getActive());
        }
        return sb.toString().trim();
    }

    public static String checkCompany(Company company, CompanyUpdateDto companyUpdateDto) {
        StringBuilder sb = new StringBuilder();
        if( validateStringNoRequiredUpdate(company.getName(), companyUpdateDto.getName()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.name,
                    company.getName(), companyUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            company.setName(companyUpdateDto.getName());
        }

        if( validateStringNoRequiredUpdate(company.getRfc(), companyUpdateDto.getRfc()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.rfc,
                    company.getRfc(), companyUpdateDto.getRfc())).append(GeneralKeys.JUMP_LINE);
            company.setRfc(companyUpdateDto.getRfc());
        }

        if( validateStringNoRequiredUpdate(company.getAddress(), companyUpdateDto.getAddress()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.address,
                    company.getAddress(), companyUpdateDto.getAddress())).append(GeneralKeys.JUMP_LINE);
            company.setAddress(companyUpdateDto.getAddress());
        }

        if( validateStringNoRequiredUpdate(company.getPhone(), companyUpdateDto.getPhone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.phone,
                    company.getPhone(), companyUpdateDto.getPhone())).append(GeneralKeys.JUMP_LINE);
            company.setPhone(companyUpdateDto.getPhone());
        }

        if( ( company.getType() == null && companyUpdateDto.getType() != null )
                || ( company.getType() != null && companyUpdateDto.getType() != null && !company.getType().equals(companyUpdateDto.getType()) ) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.type,
                    company.getType(), companyUpdateDto.getType())).append(GeneralKeys.JUMP_LINE);
            company.setType(companyUpdateDto.getType());
        }

        if( validateStringNoRequiredUpdate(company.getEmailDomain(), companyUpdateDto.getEmailDomain()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.emailDomain,
                    company.getEmailDomain(), companyUpdateDto.getEmailDomain())).append(GeneralKeys.JUMP_LINE);
            company.setEmailDomain(companyUpdateDto.getEmailDomain());
        }

        if( validateBooleanNoRequiredUpdate(company.getActive(), companyUpdateDto.getActive()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, CompanyUpdateDto.Fields.active,
                    company.getActive(), companyUpdateDto.getActive())).append(GeneralKeys.JUMP_LINE);
            company.setActive(companyUpdateDto.getActive());
        }

        return sb.toString().trim();
    }
}
