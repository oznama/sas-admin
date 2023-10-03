package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.User;
import com.mexico.sas.admin.api.model.UserConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Oziel Naranjo
 */
public interface UserConfirmationTokenRepository extends JpaRepository<UserConfirmationToken, Long> {

    Optional<UserConfirmationToken> findByConfirmationToken(String confirmationToken);

//    Optional<UserConfirmationToken> findByConfirmationTokenAndValidatedFalse(String confirmationToken);

//    Optional<UserConfirmationToken> findByConfirmationTokenAndValidatedTrueAndUsedFalse(String confirmationToken);
    Optional<UserConfirmationToken> findByConfirmationTokenAndUsedFalse(String confirmationToken);

    Optional<UserConfirmationToken> findByUser(User user);
}
