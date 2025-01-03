package kkito.reagent_order.login

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kkito.reagent_order.app_user.service.AppUserService
import kkito.reagent_order.error.BusinessLogicException
import kkito.reagent_order.error.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val appUserService: AppUserService
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
        val token = extractToken(request)
        val appUserId = jwtUtil.extractAppUserId(token)
        val appUserEntity = appUserService.getAppUser(appUserId)

        // ユーザー情報を基に認証オブジェクトを作成
        val authentication = UsernamePasswordAuthenticationToken(appUserEntity, null, appUserEntity.authorities)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
    private fun extractToken(request: HttpServletRequest): String {
        val bearer = request.getHeader("Authorization")
            ?: throw BusinessLogicException(HttpStatus.BAD_REQUEST, ErrorCode.E0007)
        return bearer.substring(7)
    }
}