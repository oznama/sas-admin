package com.mexico.sas.admin.api.security;

import com.mexico.sas.admin.api.exception.LoginException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomJWT {

  @Value("${api.security.jwt.secretId}")
  private String id;
  @Value("${api.security.jwt.secretKey}")
  private String key;
  @Value("${api.security.jwt.expiration}")
  private Long expiration;

  public String getToken(String username) throws LoginException {
    log.debug("Creating token for {}", username);
    try {
      Date creationDate = new Date(System.currentTimeMillis());
      Date expirationDate = new Date(System.currentTimeMillis() + expiration);
      log.debug(" ::: Token created at {} ::: ", creationDate);
      log.debug(" ::: Token expire to {} ::: ", expirationDate);
      return Jwts.builder().setId(id).setSubject(username)
          .setIssuedAt(creationDate)
          .setExpiration(expirationDate)
          .signWith(SignatureAlgorithm.HS512, key.getBytes(StandardCharsets.UTF_8)).compact();
    } catch (Exception e) {
      log.error(String.format("Error creating token, error: %s", e.getMessage()), e);
      throw new LoginException(e.getMessage());
    }
  }

  public Claims getClaims(String token) {
    log.debug("Getting token's claims, security key: {}", key);
    return Jwts.parser()
            .setSigningKey(key.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();
  }


}
