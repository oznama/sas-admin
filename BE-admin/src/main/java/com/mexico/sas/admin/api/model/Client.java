package com.mexico.sas.admin.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@DynamicInsert
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    private String name;
    @Column(columnDefinition = "boolean default true")
    private Boolean active;
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private Long userId;

    public Client(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "client")
    private List<Employee> employess;
}
