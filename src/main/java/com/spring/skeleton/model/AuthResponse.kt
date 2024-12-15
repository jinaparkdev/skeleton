package com.spring.skeleton.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class AuthResponse(
    val token: String,
    val company: Company
)

data class CustomUserDetails(
    val id: Long,
    val email: String,
    val pwd: String,
    val auths: MutableCollection<Authority>
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return auths
    }

    override fun getPassword(): String {
        return pwd
    }

    override fun getUsername(): String {
        return email
    }
}

enum class Authority : GrantedAuthority {
    Company,
    Admin;

    override fun getAuthority(): String {
        return name
    }
}
