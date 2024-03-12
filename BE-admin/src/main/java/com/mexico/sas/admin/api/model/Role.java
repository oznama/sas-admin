package com.mexico.sas.admin.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "sso_role")
@Data
@NoArgsConstructor
@DynamicInsert
public class Role implements Serializable {

  private static final long serialVersionUID = 1012891236169488092L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;
  private String name;
  private String description;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(columnDefinition = "timestamp default current_timestamp")
  private Date creationDate;
  private Long createdBy;
  private Boolean active;
  private Boolean eliminate;

  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<RolePermission> permissions;

  @OneToMany(mappedBy = "role")
  private List<User> users;

  public Role(Long id){
    this.id=id;
  }

}
