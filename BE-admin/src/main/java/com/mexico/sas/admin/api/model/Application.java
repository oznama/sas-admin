package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class Application implements Serializable {

    private static final long serialVersionUID = -2757724277804415473L;

    @Id
    @Column(updatable = false, nullable = false, name="app_name", length = 50)
    private String name;
    @Column(length = 255)
    private String description;

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

    public Application(String name) {
        this.name = name;
    }
}
