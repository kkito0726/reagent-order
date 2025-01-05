package kkito.reagent_order.login

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ErrorResponse
import kkito.reagent_order.error.HttpException
import kkito.reagent_order.login.repository.LoginRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val loginRepository: LoginRepository,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI == "/app_user/create" || request.requestURI == "/api/auth/login") {
            filterChain.doFilter(request, response) // スキップ
            return
        }
        try {
            val token = extractToken(request)
            val appUserId = jwtUtil.extractAppUserId(token)
            val appUserEntity = loginRepository.getAppUserByAppUserId(appUserId)

            // ユーザー情報を基に認証オブジェクトを作成
            val authentication =
                UsernamePasswordAuthenticationToken(appUserEntity, null, appUserEntity.authorities)
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (e: HttpException) {
            response.status = e.httpStatus.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(e.errorCode.code, e.errorCode.message)
                )
            )
        }

    }

    private fun extractToken(request: HttpServletRequest): String {
        val bearer = request.getHeader("Authorization")
            ?: throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0010)
        return bearer.substring(7)
    }
}