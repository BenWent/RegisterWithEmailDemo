package fhq.demo.util;

import fhq.demo.bean.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fhq
 * @date 2019/12/20 11:09
 */
@PropertySource("classpath:customer.properties")
@Component
@Slf4j
public class JwtUtil {
    private static String secret;
    private static long expiration;

    @Value("${fhq.demo.jwt.secret}")
    public void setSecret(String secret) {
        JwtUtil.secret = secret;
    }

    @Value("${fhq.demo.jwt.expiration}")
    public void setExpiration(long expiration) {
        JwtUtil.expiration = expiration;
    }

    /**
     * @param user 用户详细信息
     * @return token
     */
    public static String generateToken(User user) {
        if (user == null) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>(16);
        claims.put("sub", user.getEmail());
        claims.put("created", currentDate());

        return generateToken(claims);
    }

    /**
     * 根据 claims 生成 Token
     *
     * @param claims token中需要包含的信息
     * @return token
     */
    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(getExpiredDate())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * @return 过期时间
     */
    private static Date getExpiredDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * @param token token
     * @return claims
     */
    private static Claims getClaimsFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    /**
     * @param token token
     * @return email
     */
    public static String getEmailFromToken(String token) {
        String email;

        try {
            final Claims claims = getClaimsFromToken(token);
            email = claims.getSubject();
        } catch (Exception e) {
            email = null;
        }
        return email;
    }

    /**
     * 根据token本身提供的信息来验证token是否有效
     *
     * @param token 待验证的token
     * @return token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);   //通过密钥验证Token
            return true;
        } catch (SignatureException e) {                                     //签名异常
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {                                 //JWT格式错误
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {                                   //JWT过期
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {                               //不支持该JWT
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {                              //参数错误异常
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    /**
     * @param token token
     * @return 封装在 token 中的 token 创建时间
     */
    public static Date getCreatedDateFromToken(String token) {
        Date createdDate;

        try {
            final Claims claims = getClaimsFromToken(token);
            createdDate = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            createdDate = null;
        }
        return createdDate;
    }

    /**
     * @param token token
     * @return 封装在 token 中的 token 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Date expireDate;

        try {
            final Claims claims = getClaimsFromToken(token);
            expireDate = claims.getExpiration();
        } catch (Exception e) {
            expireDate = null;
        }
        return expireDate;
    }

    /**
     * @param token token
     * @return 当前时间是否在封装在 token 中的过期时间之后，若是，则判定为 token 过期
     */
    private static boolean isTokenExpired(String token) {
        final Date expireDate = getExpirationDateFromToken(token);

        if (expireDate == null) {
            return false;
        }
        return expireDate.before(currentDate());
    }

    /**
     * @param tokenCreatedDate    token创建时间
     * @param passwordUpdatedDate 密码最近一次更新的时间
     * @return token 是否是在最后一次修改用户信息之前创建的（信息修改之前生成的 token 即使没过期也判断为无效）
     */
    private static boolean isUserDetailsChangedBeforeTokenCreated(Date tokenCreatedDate, Date passwordUpdatedDate) {
        return tokenCreatedDate != null
                && passwordUpdatedDate != null
                && passwordUpdatedDate.before(tokenCreatedDate);
    }

    /**
     * @return 当前时间
     */
    private static Date currentDate() {
        return new Date();
    }
}
