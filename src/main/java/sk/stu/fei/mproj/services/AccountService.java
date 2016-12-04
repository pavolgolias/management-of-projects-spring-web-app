package sk.stu.fei.mproj.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.stu.fei.mproj.configuration.ApplicationProperties;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dao.AccountDao;
import sk.stu.fei.mproj.domain.dao.DaoBase;
import sk.stu.fei.mproj.domain.dto.LoginResponse;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.RecoverPasswordRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdatePasswordRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.enums.AccountRole;
import sk.stu.fei.mproj.security.*;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.util.*;

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
    private final ApplicationProperties applicationProperties;
    private final MailService mailService;
    private final StorageService storageService;

    @Autowired
    public AccountService(TokenUtils tokenUtils, Mapper mapper, AccountDao accountDao, AuthenticatedUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AuthorizationManager authorizationManager,
                          ApplicationProperties applicationProperties, MailService mailService, StorageService storageService) {
        this.tokenUtils = tokenUtils;
        this.mapper = mapper;
        this.accountDao = accountDao;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.authorizationManager = authorizationManager;
        this.applicationProperties = applicationProperties;
        this.mailService = mailService;
        this.storageService = storageService;
    }

    private <T, ID> T getOrElseThrowEntityNotFoundEx(ID id, DaoBase<T, ID> dao, String exceptionMessage) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw new EntityNotFoundException(exceptionMessage);
        }
        return item;
    }

    private Account getAccountByEmail(String email) {
        final Account account = accountDao.findByEmail(email);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account email=%s not found", email));
        }
        return account;
    }

    private Account getAccountByActionToken(String actionToken) {
        final Account account = accountDao.findByActionToken(actionToken);
        if ( account == null ) {
            throw new EntityNotFoundException(String.format("Account with token=%s not found.", actionToken));
        }
        return account;
    }

    public void authenticate(String email, String password) throws AuthenticationException {
        Account account = getAccountByEmail(email);
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

    private void setActionToken(@NotNull Account account) {
        Objects.requireNonNull(account);

        account.setActionToken(RandomStringUtils.randomAlphanumeric(12));
        account.setActionTokenValidUntil(DateTime.now().plusDays(7).toDate());
    }

    private void eraseActionToken(@NotNull Account account) {
        Objects.requireNonNull(account);

        account.setActionToken(null);
        account.setActionTokenValidUntil(null);
    }

    public Account createAccount(@NotNull CreateAccountRequestDto dto) throws MessagingException, MalformedURLException {
        Objects.requireNonNull(dto);

        if ( accountDao.findByEmail(dto.getEmail()) != null ) {
            throw new IllegalArgumentException(String.format("Email=%s is already used by another account.", dto.getEmail()));
        }
        Account account = mapper.toAccount(dto);
        account.setActive(false);
        setActionToken(account);
        account.setRole(AccountRole.StandardUser);
        if ( !dto.getPassword().equals(dto.getRepeatPassword()) ) {
            throw new IllegalArgumentException("Password and repeat password must be same.");
        }
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        if ( dto.getStaticAvatarFilename() == null ) {
            account.setStaticAvatarFilename("images/icons/avatars/avatar1.png");
        }
        accountDao.persist(account);

        Map<String, String> model = new HashMap<>();
        model.put("avenirNextRegularFontUrl", applicationProperties.buildFrontendUrl("/fonts/AvenirNextLTPro-Regular.otf").toString());
        model.put("avenirNextItalicFontUrl", applicationProperties.buildFrontendUrl("/fonts/AvenirNextLTPro-UltLtIt.otf").toString());
        model.put("projectsLogoUrl", applicationProperties.buildFrontendUrl("/images/logos/logo_black_new.png").toString());
        model.put("activationUrl", applicationProperties.buildFrontendUrl("/account_management.html?activateAccount=" + account.getActionToken()).toString());
        model.put("discardUrl", applicationProperties.buildFrontendUrl("/account_management.html?discardAccount=" + account.getActionToken()).toString());
        mailService.sendHtmlEmail(account.getEmail(), "Projects: Account activation", "account-activation", model);

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
    public List<Account> suggestAccounts(String searchKey, Long limit, List<Long> idsToExclude) {
        return accountDao.findAllBySearchKeyLimitBy(searchKey, limit, idsToExclude);
    }

    public void activateAccount(String actionToken) {
        final Account account = getAccountByActionToken(actionToken);
        if ( account.getActionTokenValidUntil().before(new Date()) ) {
            throw new IllegalStateException("Token is not valid.");
        }
        if ( account.getDeletedAt() != null ) {
            throw new IllegalStateException("Account was deleted.");
        }
        if ( account.getActive() ) {
            throw new IllegalStateException("Account is already active.");
        }
        account.setActive(true);
        eraseActionToken(account);
        accountDao.persist(account);
    }

    public void discardUnactivatedAccount(String actionToken) {
        final Account account = getAccountByActionToken(actionToken);
        if ( account.getActive() ) {
            throw new IllegalStateException("Account was already activated. You can discard only unactivated account");
        }
        if ( account.getDeletedAt() != null ) {
            throw new IllegalStateException("Account was already activated. You can discard only unactivated account");
        }
        accountDao.purge(account);
    }

    public void requestAccountRecovery(String email) throws MalformedURLException, MessagingException {
        final Account account = getAccountByEmail(email);
        setActionToken(account);
        accountDao.persist(account);

        Map<String, String> model = new HashMap<>();
        model.put("avenirNextRegularFontUrl", applicationProperties.buildFrontendUrl("/fonts/AvenirNextLTPro-Regular.otf").toString());
        model.put("avenirNextItalicFontUrl", applicationProperties.buildFrontendUrl("/fonts/AvenirNextLTPro-UltLtIt.otf").toString());
        model.put("projectsLogoUrl", applicationProperties.buildFrontendUrl("/images/logos/logo_black_new.png").toString());
        model.put("recoverUrl", applicationProperties.buildFrontendUrl("/account_management.html?recoverAccount=" + account.getActionToken()).toString());
        model.put("discardUrl", applicationProperties.buildFrontendUrl("/account_management.html?discardAccountRecovery=" + account.getActionToken()).toString());
        mailService.sendHtmlEmail(account.getEmail(), "Projects: Account recovery", "account-recovery", model);
    }

    public void discardAccountRecovery(String actionToken) {
        final Account account = getAccountByActionToken(actionToken);
        eraseActionToken(account);
        accountDao.persist(account);
    }

    public void recoverAccount(String actionToken, @NotNull RecoverPasswordRequestDto dto) {
        Objects.requireNonNull(dto);

        final Account account = getAccountByActionToken(actionToken);
        if ( account.getActionTokenValidUntil().before(new Date()) ) {
            throw new IllegalStateException("Token is not valid.");
        }
        if ( !dto.getNewPassword().equals(dto.getRepeatNewPassword()) ) {
            throw new IllegalArgumentException("Password and repeat password must be same.");
        }
        account.setActive(true);
        account.setDeletedAt(null);
        account.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        eraseActionToken(account);
        accountDao.persist(account);
    }

    @RoleSecured
    public void saveAccountAvatar(Long accountId, MultipartFile file) {
        final Account account = getAccount(accountId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                account,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update account id=%d photo", accountId)
        );
        String newFileName = account.getAccountId() + "_"
                + account.getEmail().substring(0, account.getEmail().indexOf('@'))
                + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        if ( account.getAvatarFilename() != null ) {
            storageService.delete(account.getAvatarFilename());
        }
        storageService.store(file, newFileName);
        account.setAvatarFilename(newFileName);
        accountDao.persist(account);
    }

    public Resource loadAccountAvatar(Long accountId) {
        final Account account = getAccount(accountId);
        if ( account.getAvatarFilename() == null ) {
            throw new StorageService.FileNotFoundException(String.format("Avatar for account id=%d not found.", accountId));
        }
        return storageService.loadAsResource(account.getAvatarFilename());
    }

    @RoleSecured
    public void deleteAccountAvatar(Long accountId) {
        final Account account = getAccount(accountId);
        checkUpdateEligibilityOrElseThrowSecurityEx(
                account,
                authorizationManager.getCurrentAccount(),
                String.format("You are not eligible to update account id=%d photo", accountId)
        );
        if ( account.getAvatarFilename() != null ) {
            storageService.delete(account.getAvatarFilename());
        }
        account.setAvatarFilename(null);
        accountDao.persist(account);
    }
}
