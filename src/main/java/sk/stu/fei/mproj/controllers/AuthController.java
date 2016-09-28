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
import sk.stu.fei.mproj.domain.dto.DataResponse;
import sk.stu.fei.mproj.domain.dto.LoginRequest;
import sk.stu.fei.mproj.domain.dto.LoginResponse;
import sk.stu.fei.mproj.services.AccountService;

import javax.validation.Valid;

@RestController
@Transactional
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation(value = "Attempt to sign in")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @RequestMapping(value = "/attempt", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponse<LoginResponse> signIn(@RequestBody @Valid LoginRequest loginRequest) {
        accountService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return new DataResponse<>(accountService.createLoginResponse(loginRequest.getEmail()));
    }
}
