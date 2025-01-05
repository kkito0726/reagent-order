package kkito.reagent_order.login

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CustomAuthenticationToken(
    private val principal: Any,
    private val credentials: Any?,
    authorities: Collection<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any? = credentials
    override fun getPrincipal(): Any = principal
}
