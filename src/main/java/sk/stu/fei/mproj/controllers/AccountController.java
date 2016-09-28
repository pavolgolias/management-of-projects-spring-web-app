package sk.stu.fei.mproj.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.stu.fei.mproj.domain.Mapper;
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
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
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 422, message = "Unprocessable")
    })
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<AccountDto> signUp(@RequestBody @Valid CreateAccountRequestDto dto) {
        Account account = accountService.createAccount(dto);
        return new DataResponse<>(mapper.toAccountDto(account));
    }

    @ApiOperation(value = "Get information about currently logged user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RoleSecured
    public DataResponse<AccountDto> getSelf() {
        Account account = authorizationManager.getCurrentAccount();
        return new DataResponse<>(mapper.toAccountDto(account));
    }
}
