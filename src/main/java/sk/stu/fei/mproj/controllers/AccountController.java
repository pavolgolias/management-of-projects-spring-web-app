package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdatePasswordRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.security.AuthorizationManager;
import sk.stu.fei.mproj.security.RoleSecured;
import sk.stu.fei.mproj.services.AccountService;

import javax.validation.Valid;

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
    public DataResponse<AccountDto> signUp(@RequestBody @Valid CreateAccountRequestDto dto) {
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
}
