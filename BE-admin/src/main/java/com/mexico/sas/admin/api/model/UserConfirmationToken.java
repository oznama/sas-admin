package com.mexico.sas.admin.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * @author Oziel Naranjo
 */
@Entity
@Table
@Data
public class UserConfirmationToken implements Serializable {

    private static final long serialVersionUID = 2728444330523787521L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String confirmationToken;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
//    private Boolean validated;
    private Boolean expired;

    private Boolean used;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    public UserConfirmationToken(){

    }

    public UserConfirmationToken(User user) {
        this.user = user;
        update();
    }

    public void update() {
        creationDate = new Date();
//        validated = Boolean.FALSE;
        expired = Boolean.FALSE;
        used = Boolean.FALSE;
        confirmationToken = UUID.randomUUID().toString();
    }
}
