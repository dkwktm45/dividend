package com.example.dividend.security;

import com.example.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
  private static final String KEY_ROLES = "roles";
  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
  @Value("{spring.jwt.secret}")
  private String securetKey;
  private final MemberService memberService;

  public String generateToken(String username, List<String> roles) {
    Claims claims = Jwts.claims()
        .setSubject(username);
    claims.put(KEY_ROLES, roles);

    var now = new Date();
    var expriedDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);


    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expriedDate)
        .signWith(SignatureAlgorithm.HS512, this.securetKey)
        .compact();
  }

  public Authentication getAuthentication(String jwt) {
    UserDetails userDetails = memberService.loadUserByUsername(getUserName(jwt));
    return new UsernamePasswordAuthenticationToken(userDetails, "",
        userDetails.getAuthorities());
  }
  public String getUserName(String token) {
    return parseClaims(token).getSubject();
  }

  public boolean vaildateToken(String token) {
    if (!StringUtils.hasText(token)) return false;

    var claims = this.parseClaims(token);
    return !claims.getExpiration().before(new Date());
  }
  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(this.securetKey)
          .parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}
