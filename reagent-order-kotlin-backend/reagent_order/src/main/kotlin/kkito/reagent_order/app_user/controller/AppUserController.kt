package kkito.reagent_order.app_user.controller

import kkito.reagent_order.app_user.entity.AppUserEntity
import kkito.reagent_order.app_user.service.AppUserService
import kkito.reagent_order.app_user.value.*
import kkito.reagent_order.util.ControllerUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.time.LocalDateTime
import java.util.*

@Controller
class AppUserController(
    private val appUserService: AppUserService,
) : ControllerUtil() {
    @PostMapping("/app_user/create")
    fun createAppUser(@RequestBody appUserRequest: AppUserRequest): ResponseEntity<AppUserResponse> {
        val appUser = AppUserEntity(
            AppUserId(UUID.randomUUID()),
            AppUserName(appUserRequest.appUserName),
            Email(appUserRequest.email),
            Password(appUserRequest.password),
            LocalDateTime.now(),
            null
        )

        return ResponseEntity.ok(appUserService.createAppUser(appUser))
    }

    @PutMapping("app_user/{id}")
    fun updateAppUser(
        @PathVariable id: UUID,
        @RequestBody appUserRequest: AppUserRequest
    ): ResponseEntity<AppUserResponse> {
        val authAppUserEntity = user()
        val newAppUserDto = AppUserDto(
            AppUserId(id),
            AppUserName(appUserRequest.appUserName),
            Email(appUserRequest.email),
            Password(appUserRequest.password),
        )
        return ResponseEntity.ok(appUserService.updateAppUser(newAppUserDto, authAppUserEntity))
    }

    @DeleteMapping("app_user/{id}")
    fun deleteAppUser(@PathVariable id: UUID): ResponseEntity<Any> {
        val authAppUserEntity = user()
        return ResponseEntity.ok(appUserService.deleteAppUser(AppUserId(id), authAppUserEntity))
    }
}