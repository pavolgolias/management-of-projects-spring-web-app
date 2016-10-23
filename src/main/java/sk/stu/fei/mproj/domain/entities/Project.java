package sk.stu.fei.mproj.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(of = "projectId")
@ToString(of = {"projectId", "name"})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deletedAt;

    @ManyToMany
    @JoinTable
    private Set<Account> participants;

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks;
}


