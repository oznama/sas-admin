package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Oziel Naranjo
 */
@Entity
@Table
@Data
@NoArgsConstructor
@DynamicInsert
public class Catalog implements Serializable {

    private static final long serialVersionUID = -6489856986708125925L;

    @Id
    @Column(updatable = false, nullable = false)
    private Long id;
    private String value;
    private String description;
    private Long status;
    private Long companyId;
    private Boolean internal;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;

    @ManyToOne
    @JoinColumn
    private Catalog catalogParent;

    public Catalog(Long id) {
        this.id = id;
    }
}
