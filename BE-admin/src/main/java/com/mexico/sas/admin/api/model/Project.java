package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@DynamicInsert
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long id;
    @Column(name = "p_key", length = 15)
    private String key;
    @Column(length = 255)
    private String description;
    private Long status;
    private Date installationDate;

    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "boolean default current_timestamp")
    private Date creationDate;
    private Long createdBy;

    @ManyToOne
    @JoinColumn
    private Company company;

    @ManyToOne
    @JoinColumn
    private Employee projectManager;

    public Project(Long id) {
        this.id = id;
    }
}
