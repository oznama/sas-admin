package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "project_applications")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class ProjectApplication implements Serializable {

    private static final long serialVersionUID = -4308295026871326435L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private Integer hours;
    private Date startDate;
    private Date designDate;
    private Long designStatus;
    private Date developmentDate;
    private Long developmentStatus;
    private Date endDate;
    private Long endStatus;
    private String observations;
    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;

    @ManyToOne
    @JoinColumn(name = "p_key")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "app_name")
    private Application application;

    @ManyToOne
    @JoinColumn
    private Employee leader;

    @ManyToOne
    @JoinColumn
    private Employee developer;

    public ProjectApplication(Long id) {
        this.id = id;
    }
}
