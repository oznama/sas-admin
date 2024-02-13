package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@DynamicInsert
@FieldNameConstants
@ToString
public class Invoice implements Serializable {

    private static final long serialVersionUID = -1754201312242853067L;

    @Id
    @Column(updatable = false, nullable = false)
    private String invoiceNum;
    private Date issuedDate;
    private Date paymentDate;
    private Integer percentage;
    private Long status;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal total;
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
    @JoinColumn(name = "order_num")
    private Order order;

    public Invoice(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }
}
