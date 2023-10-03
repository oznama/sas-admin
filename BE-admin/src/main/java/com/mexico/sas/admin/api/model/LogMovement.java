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
public class LogMovement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    private String tableName;
    private String recordId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private Long userId;
    private Long eventId;
    private Long detailId;

}
