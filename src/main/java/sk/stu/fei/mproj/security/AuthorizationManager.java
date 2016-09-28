package sk.stu.fei.mproj.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sk.stu.fei.mproj.domain.dao.AccountDao;
import sk.stu.fei.mproj.domain.entities.Account;

@Component
public class AuthorizationManager {
    private final AccountDao accountDao;

    @Autowired
    public AuthorizationManager(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Account getCurrentAccount() throws SecurityException {
        return getCurrentAccount(false);
    }

    private Account getCurrentAccount(boolean suppressException) throws SecurityException {

        Account current = null;

        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if ( securityContext != null ) {
            final Authentication authentication = securityContext.getAuthentication();
            if ( authentication != null ) {
                if ( authentication instanceof AuthenticatedUserDetails ) {
                    current = ((AuthenticatedUserDetails) authentication).getAccount();
                }
                else if ( authentication.getPrincipal() instanceof AuthenticatedUserDetails ) {
                    current = ((AuthenticatedUserDetails) authentication.getPrincipal()).getAccount();
                }
            }
        }

        if ( (current == null) && !(suppressException) ) {
            throw new SecurityException("No authenticated account found in session.");
        }

        return current != null ? accountDao.findById(current.getId()) : null;
    }
}
