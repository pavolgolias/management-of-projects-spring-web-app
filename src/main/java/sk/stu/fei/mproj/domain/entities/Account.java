package sk.stu.fei.mproj.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import sk.stu.fei.mproj.domain.enums.AccountRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "accountId")
@ToString(of = {"accountId", "email"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    @NotNull
    private String firstName;

    @Column(nullable = false)
    @NotNull
    private String lastName;

    @Column(nullable = false, unique = true, updatable = false)
    @Email
    @NotNull
    private String email;

    @Column
    @NotNull
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountRole role;

    @Column(nullable = false)
    @NotNull
    private Boolean active;

    @Column(length = 128, unique = true)
    private String actionToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date actionTokenValidUntil;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deletedAt;

    /**
     * Projects where this account is administrator account.
     * This is not owning side of the JPA relation!
     */
    @ManyToMany(mappedBy = "administrators")
    private Set<Project> administeredProjects;

    /**
     * Projects where this account is participant account.
     * This is not owning side of the JPA relation!
     */
    @ManyToMany(mappedBy = "participants")
    private Set<Project> participatedProjects;
}
