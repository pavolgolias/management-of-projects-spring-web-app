package sk.stu.fei.mproj.domain;

import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.stu.fei.mproj.domain.dto.account.AccountDto;
import sk.stu.fei.mproj.domain.dto.account.CreateAccountRequestDto;
import sk.stu.fei.mproj.domain.dto.account.UpdateAccountRequestDto;
import sk.stu.fei.mproj.domain.entities.Account;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
public class Mapper {
    private final MapperFactory mapperFactory;

    @Autowired
    public Mapper(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.configure(mapperFactory);
    }

    private void configure(MapperFactory mapperFactory) {
        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(CreateAccountRequestDto.class, Account.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdateAccountRequestDto.class, Account.class)
                .byDefault()
                .register();
    }

    public AccountDto toAccountDto(@NotNull Account account) {
        Objects.requireNonNull(account);

        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

    public Account toAccount(@NotNull CreateAccountRequestDto dto) {
        Objects.requireNonNull(dto);

        return mapperFactory.getMapperFacade().map(dto, Account.class);
    }

    public void fillAccount(@NotNull UpdateAccountRequestDto dto, @NotNull Account account) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(account);

        mapperFactory.getMapperFacade().map(dto, account);
    }
}
