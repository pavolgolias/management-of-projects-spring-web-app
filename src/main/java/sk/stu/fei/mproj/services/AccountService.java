package sk.stu.fei.mproj.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dao.AccountDao;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dto.LoginResponse;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdatePasswordRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.enums.AccountRole;
import sk.stu.fei.mproj.security.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AccountService {
    private final AccountDao accountDao;
    private final AuthenticatedUserDetailsService userDetailsService;
    private final TokenUtils tokenUtils;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthorizationManager authorizationManager;

    @Autowired
    public AccountService(TokenUtils tokenUtils, Mapper mapper, AccountDao accountDao, AuthenticatedUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AuthorizationManager authorizationManager) {
        this.tokenUtils = tokenUtils;
        this.mapper = mapper;
        this.accountDao = accountDao;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.authorizationManager = authorizationManager;
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    private Account getAccount(String email) {
        final Account account = accountDao.findByEmail(email);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account email=%s not found", email));
        }
        return account;
    }

    public void authenticate(String email, String password) throws AuthenticationException {
        Account account = getAccount(email);
        if ( !account.getActive() ) {
            throw new SecurityException(String.format("Account email=%s not activated", email));
        }
        if ( account.getDeletedAt() != null ) {
            throw new SecurityException(String.format("Account email=%s was deleted", email));
        }

        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public LoginResponse createLoginResponse(String email) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        final String token = tokenUtils.generateToken(userDetails);

        final LoginResponse result = new LoginResponse();
        result.setToken(token);
        final Account account = ((AuthenticatedUserDetails) userDetails).getAccount();
        result.setAccount(mapper.toAccountDto(account));
        result.setRole(account.getRole());

        return result;
    }

    public void setActionToken(@NotNull Account account) {
        Objects.requireNonNull(account);

        account.setActionToken(RandomStringUtils.randomNumeric(12));
        account.setActionTokenValidUntil(DateTime.now().plusDays(7).toDate());

        accountDao.persist(account);
    }

    public Account createAccount(@NotNull CreateAccountRequestDto dto) {
        Objects.requireNonNull(dto);

        if ( accountDao.findByEmail(dto.getEmail()) != null ) {
            throw new IllegalArgumentException(String.format("Email=%s is already used by another account.", dto.getEmail()));
        }
        Account account = mapper.toAccount(dto);
        //TODO set account to inactive later on
        account.setActive(true);
        account.setRole(AccountRole.StandardUser);
        if ( !dto.getPassword().equals(dto.getRepeatPassword()) ) {
            throw new IllegalArgumentException("Password and repeat password must be same.");
        }
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        accountDao.persist(account);

        return account;
    }

    @RoleSecured
    public Account getAccount(Long accountId) {
        return getOrElseThrowEntityNotFoundEx(accountId, accountDao, String.format("Account id=%d not found", accountId));
    }

    private void checkUpdateEligibilityOrElseThrowSecurityEx(Account updateTarget, Account who, String exceptionMessage) {
        if ( !updateTarget.equals(who) ) {
            throw new SecurityException(exceptionMessage);
        }
    }

    @RoleSecured
    public Account updateAccount(Long accountId, @NotNull UpdateAccountRequestDto dto) {
        Objects.requireNonNull(dto);

        Account account = getAccount(accountId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                account,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update account id=%d information", accountId)
        );
        mapper.fillAccount(dto, account);
        accountDao.persist(account);

        return account;
    }

    @RoleSecured
    public void updatePassword(Long accountId, @NotNull UpdatePasswordRequestDto dto) {
        Objects.requireNonNull(dto);

        Account account = getAccount(accountId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                account,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update account id=%d information", accountId)
        );
        authenticate(account.getEmail(), dto.getOldPassword());
        if ( !dto.getNewPassword().equals(dto.getRepeatNewPassword()) ) {
            throw new IllegalArgumentException("Password and repeat password must be same.");
        }
        account.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        accountDao.persist(account);
    }

    @RoleSecured
    public void deleteAccount(Long accountId) {
        Account account = getAccount(accountId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                account,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update account id=%d information", accountId)
        );
        accountDao.delete(account);
    }

    @RoleSecured
    public List<Account> suggestAccounts(String searchKey, Long limit) {
        return accountDao.findAllBySearchKey(searchKey, limit);
    }
}
