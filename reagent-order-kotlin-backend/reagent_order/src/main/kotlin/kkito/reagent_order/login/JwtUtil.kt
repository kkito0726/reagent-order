package kkito.reagent_order.login

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Component
class JwtUtil {
    companion object {
        private val SECRET_KEY = System.getenv("JWT_SECRET_KEY") ?: "default-secret-key"
    }

    fun generateToken(appUserId: AppUserId): String {
        // 90日後
        val expiryDate = Date.from(
            LocalDate.now().plusDays(90).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        return Jwts.builder()
            .setSubject(appUserId.toString())
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractAppUserId(token: String): AppUserId {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .body
            claims.subject.toString()
            AppUserId(UUID.fromString(claims.subject.toString()))
        } catch (e: Exception) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0007)
        }
    }
}