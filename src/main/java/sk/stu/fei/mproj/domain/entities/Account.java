package sk.stu.fei.mproj.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import sk.stu.fei.mproj.domain.enums.AccountRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "email"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column(nullable = false, unique = true)
    @Email
    @NotNull
    private String email;

    @Column
    @NotNull
    private String passwordHash;

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(nullable = false)
    @NotNull
    private Boolean active;

    @Column(length = 128, unique = true)
    private String actionToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date actionTokenValidUntil;
}
