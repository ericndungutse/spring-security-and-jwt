package com.security.demo;

import java.io.Serializable;
import java.util.Objects;

class AuthorityId implements Serializable {
    private String username; // First part of the composite key
    private String authority; // Second part of the composite key

    // Default constructor, getters, setters, equals, and hashCode

    public AuthorityId() {
    }

    public AuthorityId(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getauthority() {
        return authority;
    }

    public void setauthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AuthorityId))
            return false;
        AuthorityId that = (AuthorityId) o;
        return Objects.equals(username, that.username) && Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authority);
    }
}