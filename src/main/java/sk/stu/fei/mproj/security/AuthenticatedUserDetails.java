package sk.stu.fei.mproj.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import sk.stu.fei.mproj.domain.entities.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about authorized account.
 */
public class AuthenticatedUserDetails extends User {

    private final Account account;

    public AuthenticatedUserDetails(Account account) {
        super(account.getEmail(), (account.getPasswordHash() == null) ? "dummy" : account.getPasswordHash(), createAuthorities(account));
        this.account = account;
    }

    private static List<GrantedAuthority> createAuthorities(Account account) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().name().toUpperCase()));
        return authorities;
    }

    public Account getAccount() {
        return account;
    }
}
