package com.mexico.sas.admin.api.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "sso_roles_permissions")
@Data
@DynamicInsert
public class RolePermission implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;
  @Column(columnDefinition = "boolean default true")
  private Boolean active;
  @Column(columnDefinition = "boolean default false")
  private Boolean eliminate;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp default current_timestamp")
  private Date creationDate;
  private Long createdBy;

  @ManyToOne
  @JoinColumn(name = "permission_id")
  private Permission permission;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

}
