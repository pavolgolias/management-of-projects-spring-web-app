package sk.stu.fei.mproj.security;


import sk.stu.fei.mproj.domain.enums.AccountRole;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoleSecured {
    AccountRole[] value() default {};
}
