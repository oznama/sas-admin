package com.mexico.sas.admin.api.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table
@Data
@DynamicInsert
public class UserSecurity implements Serializable {

  private static final long serialVersionUID = 5703912587503184753L;
  @Id
  @Column(updatable = false, nullable = false)
  private Long id;
  private String password;
  @Column(columnDefinition = "boolean default false")
  private Boolean emailVerified;
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastSession;

  @OneToOne
  @MapsId
  @JoinColumn
  private User user;

}
