package com.mexico.sas.admin.api.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@FieldNameConstants
@DynamicInsert
public class User implements Serializable {

  private static final long serialVersionUID = -7336917125506159642L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;
  private String password;
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
  private Employee employee;

  @ManyToOne
  @JoinColumn
  private Role role;

  public User(Long id) {
    this.id = id;
  }

}
