package com.mexico.sas.admin.api.model;

import lombok.Data;
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
@DynamicInsert
public class LogMovement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    private String tableName;
    private Long recordId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;
    private String userFullname;
    private Long eventId;
    private String description;

}
