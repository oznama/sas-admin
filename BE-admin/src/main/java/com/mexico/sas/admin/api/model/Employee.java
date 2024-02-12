package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Column(updatable = false, nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    private String secondName;
    @Column(name = "last_name", nullable = false)
    private String surname;
    private String secondSurname;
    private String phone;
    private String image;
    private Long positionId;
    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;
    private Long companyId;
    private Long bossId;
    private String cellphone;
    private String country;
    private String city;
    private String ext;


    public Employee(Long id) {
        this.id = id;
    }
}
