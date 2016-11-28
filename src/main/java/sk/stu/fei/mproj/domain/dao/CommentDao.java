package sk.stu.fei.mproj.domain.dao;

import sk.stu.fei.mproj.domain.entities.Comment;
import sk.stu.fei.mproj.domain.entities.Task;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sk.stu.fei.mproj.domain.entities.QComment.comment;

public class CommentDao extends DaoBase<Comment, Long> {

    @Override
    public void persist(Comment entity) {
        Date now = new Date();
        if ( entity.getCreatedAt() == null ) {
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
        }
        else {
            entity.setUpdatedAt(now);
        }
        super.persist(entity);
    }

    public DataPage<List<Comment>> findCommentPageFilteredByTask(
            @NotNull Long pageSize,
            @NotNull Long startId,
            @NotNull Task task) {

        Comment startComment;
        if ( startId <= 0 ) {
            startComment = queryFactory.selectFrom(comment)
                    .orderBy(comment.createdAt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            startComment = queryFactory.selectFrom(comment)
                    .where(comment.commentId.eq(startId))
                    .fetchOne();
            if ( startComment == null ) {
                throw new EntityNotFoundException(String.format("Comment id=%d not found.", startId));
            }
        }

        if ( startComment == null ) {
            List<Comment> data = new ArrayList<>();
            return new DataPage<>(data, 0L, -1L);
        }

        List<Comment> data = queryFactory.selectFrom(comment)
                .where(comment.task.eq(task))
                .orderBy(comment.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();

        Long nextCommentId;
        if ( data.isEmpty() || data.size() < pageSize + 1 ) {
            nextCommentId = -1L;
        }
        else {
            nextCommentId = data.remove(data.size() - 1).getCommentId();
        }

        return new DataPage<>(data, (long) data.size(), nextCommentId);
//        Predicate predicate;
//        switch ( type ) {
//            case All:
//                predicate = project.deletedAt.isNull()
//                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
//                                project.createdAt.eq(startProject.getCreatedAt()));
//                break;
//            case Assigned:
//                predicate = project.deletedAt.isNull()
//                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
//                                project.createdAt.eq(startProject.getCreatedAt()))
//                        .andAnyOf(project.administrators.contains(account), project.participants.contains(account));
//                break;
//            case Created:
//                predicate = project.deletedAt.isNull()
//                        .andAnyOf(project.createdAt.before(startProject.getCreatedAt()),
//                                project.createdAt.eq(startProject.getCreatedAt()))
//                        .and(project.author.eq(account));
//                break;
//            default:
//                predicate = project.deletedAt.isNull();
//                break;
//        }
//
//        List<Project> data = queryFactory.selectFrom(project)
//                .where(predicate)
//                .orderBy(project.createdAt.desc())
//                .limit(pageSize + 1)
//                .fetch();
//
//        Long nextStartProjectId;
//        if ( data.isEmpty() || data.size() < pageSize + 1 ) {
//            nextStartProjectId = -1L;
//        }
//        else {
//            nextStartProjectId = data.remove(data.size() - 1).getProjectId();
//        }
//
//        return new DataPage<>(data, (long) data.size(), nextStartProjectId);
    }
}
