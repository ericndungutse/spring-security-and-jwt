package com.security.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "authorities")
@IdClass(AuthorityId.class)
public class Authority {
    @Id
    private String username;

    @Id
    private String authority;

    @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;

    public Authority() {
    }

    public Authority(String authority, String username) {
        this.authority = authority;
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
