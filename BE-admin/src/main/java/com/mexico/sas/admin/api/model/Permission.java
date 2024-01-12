package com.mexico.sas.admin.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table
@Data
@DynamicInsert
public class Permission implements Serializable {

  private static final long serialVersionUID = 197909150591029772L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;
  @Column(unique = true)
  private String name;
  private String description;
  @Column(columnDefinition = "boolean default true")
  private Boolean visible;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp default current_timestamp")
  private Date creationDate;
  private Long createdBy;

  @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
  private List<RolePermission> permissions;

}
