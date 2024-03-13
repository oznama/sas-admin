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
@Table(name = "projects")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class Project implements Serializable {

    private static final long serialVersionUID = -3093222154480163542L;

    @Id
    @Column(updatable = false, nullable = false, name = "p_key", length = 15)
    private String key;
    @Column(length = 255)
    private String description;
    private Long status;
    private Date installationDate;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String observations;
    private Date qaStartDate;
    private Date qaEndDate;
    private Date deliveryDate;
    private String followCode;

    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;

    @ManyToOne
    @JoinColumn
    private Company company;

    @ManyToOne
    @JoinColumn
    private Employee projectManager;

    public Project(String key) {
        this.key = key;
    }
}
