package kkito.reagent_order.app_user.controller

import kkito.reagent_order.app_user.model.*
import kkito.reagent_order.app_user.service.AppUserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime
import java.util.*

@Controller
open class AppUserController(private val appUserService: AppUserService) {
    @PostMapping("/app_user/create")
    fun createAppUser(@RequestBody appUserRequest: AppUserRequest): ResponseEntity<AppUserResponse> {
        val appUser = AppUserEntity(
            AppUserId.of(UUID.randomUUID()),
            AppUserName.of(appUserRequest.appUserName),
            Email.of(appUserRequest.email),
            Password(appUserRequest.password),
            LocalDateTime.now(),
            null
        )

        return ResponseEntity.ok(appUserService.createAppUser(appUser))
    }
}