package hexlet.code.service;

import hexlet.code.dto.AuthRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;

  public AuthService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtEncoder = jwtEncoder;
  }

  public String authenticate(AuthRequest request) {
    if (request == null
        || !StringUtils.hasText(request.getUsername())
        || !StringUtils.hasText(request.getPassword())) {
      throw new BadCredentialsException("Bad credentials");
    }

    var authentication =
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.getUsername(), request.getPassword()));

    var now = Instant.now();
    var claims =
        JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .subject(authentication.getName())
            .build();

    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }
}
