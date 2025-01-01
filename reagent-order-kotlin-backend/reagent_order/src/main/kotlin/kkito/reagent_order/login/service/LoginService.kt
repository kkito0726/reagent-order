package kkito.reagent_order.login.service

import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password
import kkito.reagent_order.login.JwtUtil
import kkito.reagent_order.login.repository.LoginRepository
import kkito.reagent_order.login.value.LoginResponse
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val loginRepository: LoginRepository,
    private val jwtUtil: JwtUtil
) {
    fun getAppUser(email: Email, password: Password): LoginResponse {
        val appUserEntity = loginRepository.getAppUser(email, password)
        return LoginResponse(
            appUserEntity,
            jwtUtil.generateToken(appUserEntity.id)
        )

    }
}