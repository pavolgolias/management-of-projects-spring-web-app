package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.account.*;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.AccountService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@Transactional
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final Mapper mapper;
    private final AuthorizationManager authorizationManager;

    @Autowired
    public AccountController(AccountService accountService, Mapper mapper, AuthorizationManager authorizationManager) {
        this.accountService = accountService;
        this.mapper = mapper;
        this.authorizationManager = authorizationManager;
    }

    @ApiOperation(value = "Sign up and create account")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<AccountDto> signUp(@RequestBody @Valid CreateAccountRequestDto dto) throws MessagingException, MalformedURLException {
        Account account = accountService.createAccount(dto);
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Get information about currently logged user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getSelf() {
        Account account = authorizationManager.getCurrentAccount();
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Get information about specified account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getAccount(@PathVariable Long accountId) {
        return new DataResponse<>(mapper.toAccountDto(accountService.getAccount(accountId)));
    }

    @ApiOperation(value = "Update information about specified account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> updateAccount(@PathVariable Long accountId, @RequestBody @Valid UpdateAccountRequestDto dto) {
        return new DataResponse<>(mapper.toAccountDto(accountService.updateAccount(accountId, dto)));
    }

    @ApiOperation(value = "Update password for specified account")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<Void> updateAccountPassword(@PathVariable Long accountId, @RequestBody @Valid UpdatePasswordRequestDto dto) {
        accountService.updatePassword(accountId, dto);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Delete specified account")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RoleSecured
    public DataResponse<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Search for users by specified search key")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<List<AccountDto>> searchAccounts(@RequestParam String searchKey, @RequestParam Long limit, @RequestBody List<Long> idsToExclude) {
        return new DataResponse<>(mapper.toAccountDtoList(accountService.suggestAccounts(searchKey, limit, idsToExclude)));
    }

    @ApiOperation(value = "Activate account specified by action token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/activate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> activateAccount(@RequestParam String token) {
        accountService.activateAccount(token);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Discard unactivated account specified by action token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/discard-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> discardUnactivatedAccount(@RequestParam String token) {
        accountService.discardUnactivatedAccount(token);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Request account recovery email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/request-recovery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> requestAccountRecovery(@RequestParam String email) throws MalformedURLException, MessagingException {
        accountService.requestAccountRecovery(email);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Discard account recovery token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/discard-recovery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> discardAccountRecovery(@RequestParam String token) {
        accountService.discardAccountRecovery(token);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Recover account specified by action token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/recover", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<Void> recoverAccount(@RequestParam String token, @RequestBody @Valid RecoverPasswordRequestDto dto) {
        accountService.recoverAccount(token, dto);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Upload account avatar image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/avatar", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<Void> uploadAccountAvatar(@PathVariable Long accountId, @RequestParam MultipartFile file) {
        accountService.saveAccountAvatar(accountId, file);
        return new DataResponse<>();
    }

    @ApiOperation(value = "Download account avatar image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/avatar", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<Resource> serveAccountAvatar(@PathVariable Long accountId) {
        Resource file = accountService.loadAccountAvatar(accountId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"avatar" + file.getFilename().substring(file.getFilename().indexOf('.')) + "\"")
                .body(file);
    }

    @ApiOperation(value = "Delete account avatar image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found")
    })
    @RequestMapping(value = "/{accountId}/avatar", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<Void> deleteAccountAvatar(@PathVariable Long accountId) {
        accountService.deleteAccountAvatar(accountId);
        return new DataResponse<>();
    }
}
