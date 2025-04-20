package com.example.naturelink.Entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN , EMPLOYEE , AGENCE , PROVIDER , MONUMENT ;
    @Override
    public String getAuthority() {
        return "ROLE_" + name();  // Prefix roles with 'ROLE_' as Spring Security expects this convention
    }
}
