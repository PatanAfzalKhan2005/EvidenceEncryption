package com.imageseal.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key; private final long expiration;
    public JwtService(@Value("${imageseal.jwt-secret}") String secret,@Value("${imageseal.jwt-expiration-ms}") long expiration){
        if(secret==null||secret.length()<32) throw new IllegalStateException("JWT_SECRET must be configured with at least 32 characters");
        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;
    }
    public String issue(String userId,String email){long now=System.currentTimeMillis();return Jwts.builder().subject(userId).claim("email",email).issuedAt(new Date(now)).expiration(new Date(now+expiration)).signWith(key).compact();}
    public Claims parse(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
}
