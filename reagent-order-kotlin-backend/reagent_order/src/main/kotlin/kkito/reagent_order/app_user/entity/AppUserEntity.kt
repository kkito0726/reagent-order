package kkito.reagent_order.app_user.entity

import kkito.reagent_order.app_user.value.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

data class AppUserEntity(
    val id: AppUserId,
    val appUserName: AppUserName,
    val email: Email,
    val password: Password,
    val createdAt: LocalDateTime,
    var deletedAt: LocalDateTime? = null,
    val role: Role
//    private val authorities: List<GrantedAuthority> = listOf()
) : UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${role.value}"))

    override fun getPassword(): String = password.value

    override fun getUsername(): String = email.value

}