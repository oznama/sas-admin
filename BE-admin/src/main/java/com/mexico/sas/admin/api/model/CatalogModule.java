package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Oziel Naranjo
 */
@Entity
@Table(name = "catalog_modules")
@Data
public class CatalogModule implements Serializable {


    private static final long serialVersionUID = 6851661693046747183L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private Long catalogModuleId;
    private Long catalogId;
    private Long userId;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

}
