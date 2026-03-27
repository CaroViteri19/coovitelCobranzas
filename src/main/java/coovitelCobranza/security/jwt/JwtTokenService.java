package coovitelCobranza.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationInSeconds;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           @Value("${security.jwt.expiration-seconds:3600}") long expirationInSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.expirationInSeconds = expirationInSeconds;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        List<String> authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("coovite-cobranzas")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationInSeconds))
                .subject(authentication.getName())
                .claim("roles", authorities)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claimsSet)).getTokenValue();
    }

    public long getExpirationInSeconds() {
        return expirationInSeconds;
    }
}


