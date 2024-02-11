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
@Table(name = "orders")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class Order implements Serializable {

    private static final long serialVersionUID = -7097931328362020896L;

    @Id
    @Column(updatable = false, nullable = false)
    private String orderNum;
    private Date orderDate;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
    private String requisition;
    private Date requisitionDate;
    private Long requisitionStatus;
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

    public Order(String orderNum) {
        this.orderNum = orderNum;
    }
}
