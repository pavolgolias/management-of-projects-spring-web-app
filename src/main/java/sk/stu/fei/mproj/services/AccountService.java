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
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.enums.AccountRole;
import sk.stu.fei.mproj.security.AuthenticatedUserDetails;
import sk.stu.fei.mproj.security.AuthenticatedUserDetailsService;
import sk.stu.fei.mproj.security.TokenUtils;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
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

    @Autowired
    public AccountService(TokenUtils tokenUtils, Mapper mapper, AccountDao accountDao, AuthenticatedUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.tokenUtils = tokenUtils;
        this.mapper = mapper;
        this.accountDao = accountDao;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String message) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(message);
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
            throw new SecurityException("Account not activated");
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

        Account account = mapper.toAccount(dto);
        //TODO set account to inactive later on
        account.setActive(true);
        //TODO account role setup
        account.setRole(AccountRole.StandardUser);
        if ( !dto.getPassword().equals(dto.getRepeatPassword()) ) {
            throw new IllegalArgumentException("Password and repeat password must be same.");
        }
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        accountDao.persist(account);

        return account;
    }
}
