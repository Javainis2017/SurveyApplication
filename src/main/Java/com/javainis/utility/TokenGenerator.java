package com.javainis.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class TokenGenerator {

    @Getter
    @Setter
    int tokenExpiration = 1;

    @Getter
    @Setter
    String salt = "retrieve";

    public String createToken( int id )
    {
        Claims claims = Jwts.claims().setSubject( String.valueOf( id ) );
        claims.put( "mailId", id);
        Date currentTime = new Date();
        currentTime.setTime( currentTime.getTime() + tokenExpiration * 60000 );
        return Jwts.builder()
                .setClaims( claims )
                .setExpiration( currentTime )
                .signWith( SignatureAlgorithm.HS512, salt.getBytes() )
                .compact();
    }

    public String readMailIdFromToken( String token )
    {
        Jwts.parser().setSigningKey( salt.getBytes() ).parseClaimsJws( token ).getSignature();
        Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey( salt.getBytes() ).parseClaimsJws( token );
        return parseClaimsJws.getBody().getSubject();
    }
}
