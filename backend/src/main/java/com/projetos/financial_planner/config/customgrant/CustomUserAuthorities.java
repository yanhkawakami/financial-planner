package com.projetos.financial_planner.config.customgrant;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserAuthorities {

	private Long userId;
	private String username;
    private String name;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserAuthorities(Long userId, String username, String name, Collection<? extends GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
        this.name = name;
		this.authorities = authorities;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}
    public String getName() { return name;}


	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
