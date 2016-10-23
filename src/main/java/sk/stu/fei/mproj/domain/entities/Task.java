package sk.stu.fei.mproj.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import sk.stu.fei.mproj.domain.enums.TaskPriority;
import sk.stu.fei.mproj.domain.enums.TaskStatus;
import sk.stu.fei.mproj.domain.enums.TaskType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(of = "taskId")
@ToString(of = {"taskId", "name"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(nullable = false)
    @NotNull
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private TaskPriority priority;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date deletedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date completionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date aimedCompletionDate;

    @Column
    private Long timeSpentOnTaskInMillis;

    @Column
    private Long timeEstimatedForTaskInMillis;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotNull
    private Project project;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotNull
    private Account creator;

    @OneToOne
    @JoinColumn
    private Account assignee;
}
