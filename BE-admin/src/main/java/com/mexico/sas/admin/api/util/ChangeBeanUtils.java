package com.mexico.sas.admin.api.util;

import com.mexico.sas.admin.api.constants.CatalogKeys;
import com.mexico.sas.admin.api.constants.GeneralKeys;
import com.mexico.sas.admin.api.dto.application.ApplicationUpdateDto;
import com.mexico.sas.admin.api.dto.catalog.CatalogUpdateDto;
import com.mexico.sas.admin.api.dto.company.CompanyUpdateDto;
import com.mexico.sas.admin.api.dto.employee.EmployeeUpdateDto;
import com.mexico.sas.admin.api.dto.invoice.InvoiceDto;
import com.mexico.sas.admin.api.dto.order.OrderDto;
import com.mexico.sas.admin.api.dto.project.ProjectApplicationUpdateDto;
import com.mexico.sas.admin.api.dto.project.ProjectUpdateDto;
import com.mexico.sas.admin.api.dto.role.RoleUpdateDto;
import com.mexico.sas.admin.api.exception.CustomException;
import com.mexico.sas.admin.api.i18n.I18nKeys;
import com.mexico.sas.admin.api.i18n.I18nResolver;
import com.mexico.sas.admin.api.model.*;
import com.mexico.sas.admin.api.service.CatalogService;
import com.mexico.sas.admin.api.service.CompanyService;
import com.mexico.sas.admin.api.service.EmployeeService;
import com.mexico.sas.admin.api.service.RoleService;
import com.mexico.sas.admin.api.service.impl.EmployeeServiceImpl;
import com.mexico.sas.admin.api.service.impl.RoleServiceImpl;
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
            String valueOld = parseHoliday(catalog.getValue(),catalog.getCatalogParent().getId(),catalog.getId());
            String valueNew = parseHoliday(catalogUpdateDto.getValue(),catalog.getCatalogParent().getId(),catalog.getId());
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Valor",
                    valueOld, valueNew)).append(GeneralKeys.JUMP_LINE);
            catalog.setValue(catalogUpdateDto.getValue());
        }
        if( validateStringNoRequiredUpdate(catalog.getDescription(), catalogUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), "Descripción",
                    catalog.getDescription(), catalogUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            catalog.setDescription(catalogUpdateDto.getDescription());
        }
        if( validateLongNoRequiredUpdate(catalog.getStatus(), catalogUpdateDto.getStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_CATALOG_UPDATE, catalog.getValue(), "Estatus",
                    catalog.getStatus(), catalogUpdateDto.getStatus())).append(GeneralKeys.JUMP_LINE);
            catalog.setStatus(catalogUpdateDto.getStatus());
        }

        return sb.toString().trim();
    }

    public static String checkProyect(Project project, ProjectUpdateDto projectUpdateDto,
                                      CompanyService companyService, EmployeeService employeeService) throws CustomException {
        StringBuilder sb = new StringBuilder();

        if( validateStringRequiredUpdate(project.getKey(), projectUpdateDto.getKey()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Clave",
                    project.getKey(), projectUpdateDto.getKey())).append(GeneralKeys.JUMP_LINE);
            project.setKey(projectUpdateDto.getKey());
        }
        if( validateStringRequiredUpdate(project.getDescription(), projectUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Descripción",
                    project.getDescription(), projectUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            project.setDescription(projectUpdateDto.getDescription());
        }
        if( validateLongRequiredUpdate(project.getCompany().getId(), projectUpdateDto.getCompanyId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Compañia",
                    companyService.findById(project.getCompany().getId()).getName(),
                    companyService.findById(projectUpdateDto.getCompanyId())))
                    .append(GeneralKeys.JUMP_LINE);
            project.setCompany(new Company(projectUpdateDto.getCompanyId()));
        }
        if( validateLongRequiredUpdate(project.getProjectManager().getId(), projectUpdateDto.getProjectManagerId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "PM",
                    getFullname(employeeService.findEntityById(project.getProjectManager().getId())),
                    getFullname(employeeService.findEntityById(projectUpdateDto.getProjectManagerId()))))
                    .append(GeneralKeys.JUMP_LINE);
            project.setProjectManager(new Employee(projectUpdateDto.getProjectManagerId()));
        }
        try {
            String currentDate = dateToString(project.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if ( validateStringNoRequiredUpdate(currentDate, projectUpdateDto.getInstallationDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Fecha de instalación",
                        currentDate, projectUpdateDto.getInstallationDate())).append(GeneralKeys.JUMP_LINE);
                project.setInstallationDate(stringToDate(projectUpdateDto.getInstallationDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking project installation dates, error: {}", e.getMessage());
        }

        if( validateStringNoRequiredUpdate(project.getObservations(), projectUpdateDto.getObservations()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_OBSERVATION_UPDATE)).append(GeneralKeys.JUMP_LINE);
            project.setObservations(projectUpdateDto.getObservations());
        }

        return sb.toString().trim();
    }

    public static String checkProjectApplication(ProjectApplication projectApplication, ProjectApplicationUpdateDto projectApplicationUpdateDto,
                                                 EmployeeService employeeService) throws CustomException {
        StringBuilder sb = new StringBuilder();
        String currentDate = null;
        if( validateStringRequiredUpdate(projectApplication.getApplication().getName(), projectApplicationUpdateDto.getApplication()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Aplicación",
                            projectApplication.getApplication().getName(), projectApplicationUpdateDto.getApplication()))
                    .append(GeneralKeys.JUMP_LINE);
            projectApplication.setApplication(new Application(projectApplicationUpdateDto.getApplication()));
        }
        double currentAmount = doubleScale(projectApplication.getAmount().doubleValue());
        double newAmount = doubleScale(projectApplicationUpdateDto.getAmount().doubleValue());
        if( currentAmount != newAmount ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Monto",
                    formatCurrency(currentAmount), formatCurrency(newAmount)))
                    .append(GeneralKeys.JUMP_LINE);
            projectApplication.setAmount(projectApplicationUpdateDto.getAmount());
            projectApplication.setTax(projectApplicationUpdateDto.getTax());
            projectApplication.setTotal(projectApplicationUpdateDto.getTotal());
        }
        if( validateLongRequiredUpdate(projectApplication.getLeader().getId(), projectApplicationUpdateDto.getLeaderId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Líder",
                    getFullname(employeeService.findEntityById(projectApplication.getLeader().getId())),
                    getFullname(employeeService.findEntityById(projectApplicationUpdateDto.getLeaderId()))))
                    .append(GeneralKeys.JUMP_LINE);
            projectApplication.setLeader(new Employee(projectApplicationUpdateDto.getLeaderId()));
        }
        if( validateLongRequiredUpdate(projectApplication.getDeveloper().getId(), projectApplicationUpdateDto.getDeveloperId()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Desarrollador",
                            getFullname(employeeService.findEntityById(projectApplication.getDeveloper().getId())),
                            getFullname(employeeService.findEntityById(projectApplicationUpdateDto.getDeveloperId()))))
                    .append(GeneralKeys.JUMP_LINE);
            projectApplication.setDeveloper(new Employee(projectApplicationUpdateDto.getDeveloperId()));
        }
        try {
            currentDate = dateToString(projectApplication.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, projectApplicationUpdateDto.getStartDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Inicio",
                        currentDate, projectApplicationUpdateDto.getStartDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setStartDate(stringToDate(projectApplicationUpdateDto.getStartDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking start date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, projectApplicationUpdateDto.getDesignDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Analisis y diseño",
                        currentDate, projectApplicationUpdateDto.getDesignDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setDesignDate(stringToDate(projectApplicationUpdateDto.getDesignDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking design date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, projectApplicationUpdateDto.getDevelopmentDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Construcción",
                        currentDate, projectApplicationUpdateDto.getDevelopmentDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setDevelopmentDate(stringToDate(projectApplicationUpdateDto.getDevelopmentDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking development date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(projectApplication.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, projectApplicationUpdateDto.getEndDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Cierre",
                        currentDate, projectApplicationUpdateDto.getEndDate())).append(GeneralKeys.JUMP_LINE);
                projectApplication.setEndDate(stringToDate(projectApplicationUpdateDto.getEndDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking end date, error: {}", e.getMessage());
        }
        if( validateStringNoRequiredUpdate(projectApplication.getObservations(), projectApplicationUpdateDto.getObservations()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_OBSERVATION_UPDATE)).append(GeneralKeys.JUMP_LINE);
            projectApplication.setObservations(projectApplicationUpdateDto.getObservations());
        }
        if( validateLongNoRequiredUpdate(projectApplication.getDesignStatus(), projectApplicationUpdateDto.getDesignStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus de diseño",
                    projectApplication.getDesignStatus(), projectApplicationUpdateDto.getDesignStatus())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDesignStatus(projectApplicationUpdateDto.getDesignStatus());
        }
        if( validateLongNoRequiredUpdate(projectApplication.getDevelopmentStatus(), projectApplicationUpdateDto.getDevelopmentStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus de desarrollo",
                    projectApplication.getDevelopmentStatus(), projectApplicationUpdateDto.getDevelopmentStatus())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setDevelopmentStatus(projectApplicationUpdateDto.getDevelopmentStatus());
        }
        if( validateLongNoRequiredUpdate(projectApplication.getEndStatus(), projectApplicationUpdateDto.getEndStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus de cierre",
                    projectApplication.getEndStatus(), projectApplicationUpdateDto.getEndStatus())).append(GeneralKeys.JUMP_LINE);
            projectApplication.setEndStatus(projectApplicationUpdateDto.getEndStatus());
        }
        return sb.toString().trim();
    }

    public static String checkOrder(Order order, OrderDto orderDto, CatalogService catalogService) throws CustomException {
        StringBuilder sb = new StringBuilder();
        String currentDate = null;
        double currentAmount = doubleScale(order.getAmount().doubleValue());
        double newAmount = doubleScale(orderDto.getAmount().doubleValue());
        if( orderDto.getAmount() != null ) {
            if( currentAmount != newAmount ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Monto",
                        formatCurrency(currentAmount), formatCurrency(newAmount))).append(GeneralKeys.JUMP_LINE);
                order.setAmount(orderDto.getAmount());
                order.setTax(orderDto.getTax());
                order.setTotal(orderDto.getTotal());
            }
        }
        try {
            currentDate = dateToString(order.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, orderDto.getOrderDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Fecha de orden",
                        currentDate, orderDto.getOrderDate())).append(GeneralKeys.JUMP_LINE);
                order.setOrderDate(stringToDate(orderDto.getOrderDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking order date, error: {}", e.getMessage());
        }

        if( validateStringRequiredUpdate(order.getRequisition(), orderDto.getRequisition()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Requisición",
                    order.getRequisition(), orderDto.getRequisition())).append(GeneralKeys.JUMP_LINE);
            order.setRequisition(orderDto.getRequisition());
        }
        try {
            currentDate = dateToString(order.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, orderDto.getRequisitionDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Fecha de requisición",
                        currentDate, orderDto.getRequisitionDate())).append(GeneralKeys.JUMP_LINE);
                order.setRequisitionDate(stringToDate(orderDto.getRequisitionDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking end date, error: {}", e.getMessage());
        }
        if( validateStringNoRequiredUpdate(order.getObservations(), orderDto.getObservations()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_OBSERVATION_UPDATE)).append(GeneralKeys.JUMP_LINE);
            order.setObservations(orderDto.getObservations());
        }
        if( validateLongNoRequiredUpdate(order.getStatus(), orderDto.getStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus de la orden",
                    catalogService.findById(order.getStatus()).getValue(),
                    catalogService.findById(orderDto.getStatus()).getValue())
            ).append(GeneralKeys.JUMP_LINE);
            order.setStatus(orderDto.getStatus());
            if( orderDto.getStatus().equals(CatalogKeys.ORDER_STATUS_CANCELED) || orderDto.getStatus().equals(CatalogKeys.ORDER_STATUS_EXPIRED) ) {
                order.setActive(false);
                order.setEliminate(true);
            } else {
                order.setActive(true);
                order.setEliminate(false);
            }
        }
        if( validateLongNoRequiredUpdate(order.getRequisitionStatus(), orderDto.getRequisitionStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus de requisición",
                    order.getRequisitionStatus() != null ? catalogService.findById(order.getRequisitionStatus()).getValue() : "Sin estatus",
                    orderDto.getRequisitionStatus() != null ? catalogService.findById(orderDto.getRequisitionStatus()).getValue() : "Sin estatus"
                    )
            ).append(GeneralKeys.JUMP_LINE);
            order.setRequisitionStatus(orderDto.getRequisitionStatus());
        }
        return sb.toString().trim();
    }

    public static String checkInvoice(Invoice invoice, InvoiceDto invoiceDto, CatalogService catalogService) throws CustomException {
        StringBuilder sb = new StringBuilder();
        String currentDate = null;
        if( invoiceDto.getAmount() != null ) {
            double currentAmount = doubleScale(invoice.getAmount().doubleValue());
            double newAmount = doubleScale(invoiceDto.getAmount().doubleValue());
            if( currentAmount != newAmount ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Monto",
                        formatCurrency(currentAmount), formatCurrency(newAmount))).append(GeneralKeys.JUMP_LINE);
                invoice.setAmount(invoiceDto.getAmount());
            }
            currentAmount = doubleScale(invoice.getTax().doubleValue());
            newAmount = doubleScale(invoiceDto.getTax().doubleValue());
            if( currentAmount != newAmount ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Iva",
                        formatCurrency(currentAmount), formatCurrency(newAmount))).append(GeneralKeys.JUMP_LINE);
                invoice.setTax(invoiceDto.getTax());
            }
            currentAmount = doubleScale(invoice.getTotal().doubleValue());
            newAmount = doubleScale(invoiceDto.getTotal().doubleValue());
            if( currentAmount != newAmount ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Total",
                        formatCurrency(currentAmount), formatCurrency(newAmount))).append(GeneralKeys.JUMP_LINE);
                invoice.setTotal(invoiceDto.getTotal());
            }
            if( !invoice.getPercentage().equals(invoiceDto.getPercentage()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Porcentaje",
                        invoice.getPercentage(), invoiceDto.getPercentage())).append(GeneralKeys.JUMP_LINE);
                invoice.setPercentage(invoiceDto.getPercentage());
            }
        }
        try {
            currentDate = dateToString(invoice.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringRequiredUpdate(currentDate, invoiceDto.getIssuedDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Fecha de emisión",
                        currentDate, invoiceDto.getIssuedDate())).append(GeneralKeys.JUMP_LINE);
                invoice.setIssuedDate(stringToDate(invoiceDto.getIssuedDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking issued date, error: {}", e.getMessage());
        }
        try {
            currentDate = dateToString(invoice.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY, true);
            if( validateStringNoRequiredUpdate(currentDate, invoiceDto.getPaymentDate()) ) {
                sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Fecha de pago",
                        currentDate, invoiceDto.getPaymentDate())).append(GeneralKeys.JUMP_LINE);
                invoice.setPaymentDate(stringToDate(invoiceDto.getPaymentDate(), GeneralKeys.FORMAT_DDMMYYYY));
            }
        } catch (CustomException e) {
            log.error("Error checking payment date, error: {}", e.getMessage());
        }
        if( validateStringNoRequiredUpdate(invoice.getObservations(), invoiceDto.getObservations()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_OBSERVATION_UPDATE)).append(GeneralKeys.JUMP_LINE);
            invoice.setObservations(invoiceDto.getObservations());
        }
        if( validateLongNoRequiredUpdate(invoice.getStatus(), invoice.getStatus()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estatus",
                    catalogService.findById(invoice.getStatus()).getValue(),
                    catalogService.findById(invoiceDto.getStatus()).getValue())
            ).append(GeneralKeys.JUMP_LINE);
            invoice.setStatus(invoiceDto.getStatus());
            if( invoiceDto.getStatus().equals(CatalogKeys.INVOICE_STATUS_CANCELED) ) {
                invoice.setActive(false);
                invoice.setEliminate(true);
            } else {
                invoice.setActive(true);
                invoice.setEliminate(false);
            }
        }
        return sb.toString().trim();
    }

    public static String checkEmployee(Employee employee, EmployeeUpdateDto employeeUpdateDto,
                                       CatalogService catalogService, EmployeeServiceImpl employeeService) throws CustomException {
        StringBuilder sb = new StringBuilder();
        if(employeeUpdateDto.getEmail() != null && !employeeUpdateDto.getEmail().equalsIgnoreCase(employee.getEmail())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Correo",
                    employee.getEmail(), employeeUpdateDto.getEmail())).append(GeneralKeys.JUMP_LINE);
            employee.setEmail(employeeUpdateDto.getEmail());
        }
        if(employeeUpdateDto.getName() != null && !employeeUpdateDto.getName().equalsIgnoreCase(employee.getName())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Nombre",
                    employee.getName(), employeeUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            employee.setName(employeeUpdateDto.getName());
        }
        if( validateStringNoRequiredUpdate(employee.getSecondName(), employeeUpdateDto.getSecondName()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Segundo nombre",
                    employee.getSecondName(), employeeUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            employee.setSecondName(employeeUpdateDto.getSecondName());
        }
        if(employeeUpdateDto.getSurname() != null && !employeeUpdateDto.getSurname().equalsIgnoreCase(employee.getSurname())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Apellido paterno",
                    employee.getSurname(), employeeUpdateDto.getSurname())).append(GeneralKeys.JUMP_LINE);
            employee.setSurname(employeeUpdateDto.getSurname());
        }
        if( validateStringNoRequiredUpdate(employee.getSecondSurname(), employeeUpdateDto.getSecondSurname()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Apellido materno",
                    employee.getSecondSurname(), employeeUpdateDto.getSecondSurname())).append(GeneralKeys.JUMP_LINE);
            employee.setSecondSurname(employeeUpdateDto.getSecondSurname());
        }
        if((employee.getCompanyId() == null && employeeUpdateDto.getCompanyId() != null)
                || (employeeUpdateDto.getCompanyId() != null && !employeeUpdateDto.getCompanyId().equals(employee.getCompanyId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Compañia",
                    employee.getCompanyId(), employeeUpdateDto.getCompanyId())).append(GeneralKeys.JUMP_LINE);
            employee.setCompanyId(employeeUpdateDto.getCompanyId());
        }

        if( validateStringNoRequiredUpdate(employee.getPhone(), employeeUpdateDto.getPhone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Telefono",
                    employee.getPhone(), employeeUpdateDto.getPhone())).append(GeneralKeys.JUMP_LINE);
            employee.setPhone(employeeUpdateDto.getPhone());
        }
        if( validateStringNoRequiredUpdate(employee.getCellphone(), employeeUpdateDto.getCellphone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Celular",
                    employee.getCellphone(), employeeUpdateDto.getCellphone())).append(GeneralKeys.JUMP_LINE);
            employee.setCellphone(employeeUpdateDto.getCellphone());
        }
        if( validateStringNoRequiredUpdate(employee.getCountry(), employeeUpdateDto.getCountry()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Pais",
                    employee.getCountry(), employeeUpdateDto.getCountry())).append(GeneralKeys.JUMP_LINE);
            employee.setCountry(employeeUpdateDto.getCountry());
        }
        if( validateStringNoRequiredUpdate(employee.getCity(), employeeUpdateDto.getCity()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Ciudad",
                    employee.getCity(), employeeUpdateDto.getCity())).append(GeneralKeys.JUMP_LINE);
            employee.setCity(employeeUpdateDto.getCity());
        }
        if( validateStringNoRequiredUpdate(employee.getExt(), employeeUpdateDto.getExt()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Extencion",
                    employee.getExt(), employeeUpdateDto.getExt())).append(GeneralKeys.JUMP_LINE);
            employee.setExt(employeeUpdateDto.getExt());
        }
        // Se cambia el jefe
        if((employee.getBossId() == null && employeeUpdateDto.getBossId() != null) || (employee.getBossId() != null && employeeUpdateDto.getBossId() == null)
                || (employee.getBossId() != null && employeeUpdateDto.getBossId() != null && !employeeUpdateDto.getBossId().equals(employee.getBossId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Jefe",
                    employee.getBossId() != null ? getFullname(employeeService.findEntityById(employee.getBossId())) : "Sin jefe asignado",
                    employeeUpdateDto.getBossId() != null ? getFullname(employeeService.findEntityById(employeeUpdateDto.getBossId())) : "Sin jefe"
            )).append(GeneralKeys.JUMP_LINE);
            employee.setBossId(employeeUpdateDto.getBossId() != null ? employeeUpdateDto.getBossId() : null);
        }
        //Se actualiza la posicion
        if((employee.getPositionId() == null && employeeUpdateDto.getPositionId() != null) || (employee.getPositionId() != null && employeeUpdateDto.getPositionId() == null)
                || (employee.getPositionId() != null && employeeUpdateDto.getPositionId() != null && !employeeUpdateDto.getPositionId().equals(employee.getPositionId()))) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Puesto",
                    employee.getPositionId() != null ? catalogService.findById(employee.getPositionId()).getValue() : "Sin puesto asignado",
                    employeeUpdateDto.getPositionId() != null ? catalogService.findById(employeeUpdateDto.getPositionId()).getValue() : "Sin puesto"
            )).append(GeneralKeys.JUMP_LINE);
            employee.setPositionId(employeeUpdateDto.getPositionId() != null ? employeeUpdateDto.getPositionId(): null);
        }

        if(employeeUpdateDto.getActive() != null && !employeeUpdateDto.getActive().equals(employee.getActive())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.active,
                    employee.getActive(), employeeUpdateDto.getActive())).append(GeneralKeys.JUMP_LINE);
            employee.setActive(employeeUpdateDto.getActive());
        }
        return sb.toString().trim();
    }

    public static String checkCompany(Company company, CompanyUpdateDto companyUpdateDto, CatalogService catalogService) throws CustomException {
        StringBuilder sb = new StringBuilder();
        if( validateStringNoRequiredUpdate(company.getName(), companyUpdateDto.getName()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Razón social",
                    company.getName(), companyUpdateDto.getName())).append(GeneralKeys.JUMP_LINE);
            company.setName(companyUpdateDto.getName());
        }

        if( validateStringNoRequiredUpdate(company.getAlias(), companyUpdateDto.getAlias()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Alias",
                    company.getAlias(), companyUpdateDto.getAlias())).append(GeneralKeys.JUMP_LINE);
            company.setAlias(companyUpdateDto.getAlias());
        }

        if( validateStringNoRequiredUpdate(company.getRfc(), companyUpdateDto.getRfc()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "RFC",
                    company.getRfc(), companyUpdateDto.getRfc())).append(GeneralKeys.JUMP_LINE);
            company.setRfc(companyUpdateDto.getRfc());
        }

        if( validateStringNoRequiredUpdate(company.getAddress(), companyUpdateDto.getAddress()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Calle",
                    company.getAddress(), companyUpdateDto.getAddress())).append(GeneralKeys.JUMP_LINE);
            company.setAddress(companyUpdateDto.getAddress());
        }

        if( validateStringNoRequiredUpdate(company.getInterior(), companyUpdateDto.getInterior()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Interior",
                    company.getInterior(), companyUpdateDto.getInterior())).append(GeneralKeys.JUMP_LINE);
            company.setInterior(companyUpdateDto.getInterior());
        }

        if( validateStringNoRequiredUpdate(company.getExterior(), companyUpdateDto.getExterior()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Exterior",
                    company.getExterior(), companyUpdateDto.getExterior())).append(GeneralKeys.JUMP_LINE);
            company.setExterior(companyUpdateDto.getExterior());
        }

        if( validateStringNoRequiredUpdate(company.getCp(), companyUpdateDto.getCp()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Codigo postal",
                    company.getCp(), companyUpdateDto.getCp())).append(GeneralKeys.JUMP_LINE);
            company.setCp(companyUpdateDto.getCp());
        }

        if( validateStringNoRequiredUpdate(company.getLocality(), companyUpdateDto.getLocality()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Localidad/Colonia",
                    company.getLocality(), companyUpdateDto.getLocality())).append(GeneralKeys.JUMP_LINE);
            company.setLocality(companyUpdateDto.getLocality());
        }

        if( validateStringNoRequiredUpdate(company.getCity(), companyUpdateDto.getCity()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Ciudad",
                    company.getCity(), companyUpdateDto.getCity())).append(GeneralKeys.JUMP_LINE);
            company.setCity(companyUpdateDto.getCity());
        }

        if( validateStringNoRequiredUpdate(company.getState(), companyUpdateDto.getState()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Estado",
                    company.getState(), companyUpdateDto.getState())).append(GeneralKeys.JUMP_LINE);
            company.setState(companyUpdateDto.getState());
        }

        if( validateStringNoRequiredUpdate(company.getCountry(), companyUpdateDto.getCountry()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Pais",
                    company.getCountry(), companyUpdateDto.getCountry())).append(GeneralKeys.JUMP_LINE);
            company.setCountry(companyUpdateDto.getCountry());
        }

        if( validateStringNoRequiredUpdate(company.getPhone(), companyUpdateDto.getPhone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Telefono",
                    company.getPhone(), companyUpdateDto.getPhone())).append(GeneralKeys.JUMP_LINE);
            company.setPhone(companyUpdateDto.getPhone());
        }

        if( validateStringNoRequiredUpdate(company.getCellphone(), companyUpdateDto.getCellphone()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Telefono celular",
                    company.getCellphone(), companyUpdateDto.getCellphone())).append(GeneralKeys.JUMP_LINE);
            company.setCellphone(companyUpdateDto.getCellphone());
        }

        if( validateStringNoRequiredUpdate(company.getExt(), companyUpdateDto.getExt()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Extencion",
                    company.getExt(), companyUpdateDto.getExt())).append(GeneralKeys.JUMP_LINE);
            company.setExt(companyUpdateDto.getExt());
        }

        if( ( company.getType() == null && companyUpdateDto.getType() != null )
                || ( company.getType() != null && companyUpdateDto.getType() != null && !company.getType().equals(companyUpdateDto.getType()) ) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Tipo",
                    catalogService.findById(company.getType()).getValue(),
                    catalogService.findById(companyUpdateDto.getType()).getValue()
            )).append(GeneralKeys.JUMP_LINE);
            company.setType(companyUpdateDto.getType());
        }

        if( validateStringNoRequiredUpdate(company.getEmailDomain(), companyUpdateDto.getEmailDomain()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Dominio de correo",
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

    public static String checkApplication(Application application, ApplicationUpdateDto applicationUpdateDto, CompanyService companyService) throws CustomException {
        StringBuilder sb = new StringBuilder();

        if( validateStringNoRequiredUpdate(application.getDescription(), applicationUpdateDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Descripcion",
                    application.getDescription(), applicationUpdateDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            application.setDescription(applicationUpdateDto.getDescription());
        }

        if( validateLongRequiredUpdate(application.getCompany().getId(), applicationUpdateDto.getCompanyId())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Empresa",
                    application.getCompany().getName(),
                    companyService.findById(applicationUpdateDto.getCompanyId()).getName()
            )).append(GeneralKeys.JUMP_LINE);
            application.setCompany(new Company(applicationUpdateDto.getCompanyId()));
        }

        return sb.toString().trim();
    }

    public static String checkRole(Role role, RoleUpdateDto roleDto, RoleService roleService) {
        StringBuilder sb = new StringBuilder();
        if(roleDto.getName() != null && !roleDto.getName().equalsIgnoreCase(role.getName())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Nombre",
                    role.getName(), roleDto.getName())).append(GeneralKeys.JUMP_LINE);
            role.setName(roleDto.getName());
        }
        if( validateStringNoRequiredUpdate(role.getDescription(), roleDto.getDescription()) ) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, "Descripcion",
                    role.getDescription(), roleDto.getDescription())).append(GeneralKeys.JUMP_LINE);
            role.setDescription(roleDto.getDescription());
        }
        if(roleDto.getActive() != null && !roleDto.getActive().equals(role.getActive())) {
            sb.append(I18nResolver.getMessage(I18nKeys.LOG_GENERAL_UPDATE, EmployeeUpdateDto.Fields.active,
                    role.getActive(), roleDto.getActive())).append(GeneralKeys.JUMP_LINE);
            role.setActive(roleDto.getActive());
        }

        return sb.toString().trim();
    }
}
