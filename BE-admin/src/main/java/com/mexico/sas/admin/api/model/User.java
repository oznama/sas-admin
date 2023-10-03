package com.mexico.sas.admin.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "users")
@Data
@FieldNameConstants
@DynamicInsert
public class User implements Serializable {

  private static final long serialVersionUID = -7336917125506159642L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;
  @Column(updatable = false, nullable = false)
  private String email;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String surname;
  private String secondSurname;
  @Column(nullable = false)
  private String phone;
  private String image;
  @Column(columnDefinition = "boolean default true")
  private Boolean active;
  @Column(columnDefinition = "boolean default false")
  private Boolean eliminate;
  private Long userId;
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne
  @JoinColumn
  private Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserSecurity userSecurity;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserConfirmationToken userConfirmationToken;

}
