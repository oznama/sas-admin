package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Oziel Naranjo
 */
@Entity
@Table
@Data
public class Catalog implements Serializable {

    private static final long serialVersionUID = -6489856986708125925L;

    @Id
    @Column(updatable = false, nullable = false)
    private Long id;
    private String value;
    private String description;
    private Boolean isRequired;
    private Long status;
    private Long type;
    private Long userId;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @JoinColumn
    private Catalog catalogParent;

}
