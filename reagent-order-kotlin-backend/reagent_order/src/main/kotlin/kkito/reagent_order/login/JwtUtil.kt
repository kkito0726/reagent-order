package kkito.reagent_order.login

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import kkito.reagent_order.app_user.value.AppUserId
import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.security.config.Elements.JWT
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Component
class JwtUtil {
    companion object {
        private val SECRET_KEY = System.getenv("JWT_SECRET_KEY") ?: "TT44yqfv2snn8JfgGOH+IJOGlgFhNTZYvvol7pghR6Y="
    }

    fun generateToken(appUserId: AppUserId): String {
        // 90日後
        val expiryDate = Date.from(
            LocalDate.now().plusDays(90).atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        return Jwts.builder()
            .setSubject(appUserId.toString())
            .claim("roles", listOf("USER"))
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    private fun ByteArray.toBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }

    private fun generateHS256SecretKey(): String {
        val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        return key.encoded.toBase64()  // Base64エンコードされた鍵を取得
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
            AppUserId(UUID.fromString(claims.subject.toString()))
        } catch (e: Exception) {
            throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0010)
        }
    }
}