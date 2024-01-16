package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "project_applications")
@Data
@NoArgsConstructor
@DynamicInsert
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false)
    private Long applicationId;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private Integer hours;
    private Date startDate;
    private Date designDate;
    private Date developmentDate;
    private Date endDate;
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
    private Project project;

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
