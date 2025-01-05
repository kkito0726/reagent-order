package kkito.reagent_order.login.controller

import kkito.reagent_order.app_user.value.Email
import kkito.reagent_order.app_user.value.Password
import kkito.reagent_order.login.service.LoginService
import kkito.reagent_order.login.value.LoginRequest
import kkito.reagent_order.login.value.LoginResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/auth")
class AuthController(
    private val loginService: LoginService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(
            loginService.getAppUser(
                Email(request.email),
                Password(request.password)
            )
        )
    }
}