package com.ferapy.security;

import com.ferapy.document.constant.Role;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class JWTUser implements UserDetails {

    private static final long serialVersionUID = -3677247933953168385L;

    private String id;
    private Date lastModified;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private boolean canListen;
    private Collection<? extends GrantedAuthority> authorities = Arrays.stream(Role.values())
            .map(Enum::name)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

}
