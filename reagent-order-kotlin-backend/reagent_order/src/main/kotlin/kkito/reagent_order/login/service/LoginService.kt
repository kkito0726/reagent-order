package kkito.reagent_order.login.service

import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password
import kkito.reagent_order.error.ErrorCode
import kkito.reagent_order.error.ForbiddenException
import kkito.reagent_order.login.JwtUtil
import kkito.reagent_order.login.repository.LoginRepository
import kkito.reagent_order.login.value.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val loginRepository: LoginRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService{
    fun getAppUser(email: Email, password: Password): LoginResponse {
        val appUserEntity = loginRepository.getAppUserByEmail(email)
        if (!passwordEncoder.matches(password.value, appUserEntity.password.value)) {
            throw ForbiddenException(HttpStatus.FORBIDDEN, ErrorCode.E0008)
        }
        return LoginResponse(
            appUserEntity,
            jwtUtil.generateToken(appUserEntity.id)
        )
    }

    override fun loadUserByUsername(email: String): UserDetails{
        return loginRepository.getAppUserByEmail(Email(email))
    }
}