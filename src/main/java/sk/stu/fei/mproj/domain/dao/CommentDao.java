package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Comment;
import sk.stu.fei.mproj.domain.entities.Task;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sk.stu.fei.mproj.domain.entities.QComment.comment;

@Repository
@Transactional
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

    public DataPage<List<Comment>> findCommentsForTaskPage(
            @NotNull Long pageSize,
            @NotNull Long startId,
            @NotNull Task task) {
        Comment startComment;
        if ( startId <= 0 ) {
            startComment = queryFactory.selectFrom(comment)
                    .where(comment.task.eq(task))
                    .orderBy(comment.createdAt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            startComment = queryFactory.selectFrom(comment)
                    .where(comment.task.eq(task)
                            .and(comment.commentId.eq(startId)))
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
                .where(comment.task.eq(task)
                        .andAnyOf(comment.createdAt.before(startComment.getCreatedAt()),
                                comment.createdAt.eq(startComment.getCreatedAt())))
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
    }
}
