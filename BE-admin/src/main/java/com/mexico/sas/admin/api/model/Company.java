package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@DynamicInsert
@FieldNameConstants
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private String name;
    private String alias;
    private String rfc;
    private String address;
    private String interior;
    private String exterior;
    private String cp;
    private String locality;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String ext;
    private String emailDomain;
    private Long type;
    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp default current_timestamp")
    private Date creationDate;
    private Long createdBy;

    public Company(Long id) {
        this.id = id;
    }
}
