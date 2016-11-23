package sk.stu.fei.mproj.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(of = "commentId")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    @NotNull
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotNull
    private Account author;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotNull
    private Task task;
}
