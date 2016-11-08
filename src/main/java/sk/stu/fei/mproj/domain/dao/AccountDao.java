package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Account;
import sk.stu.fei.mproj.domain.entities.Project;

import java.util.Date;
import java.util.List;

import static sk.stu.fei.mproj.domain.entities.QAccount.account;


@Repository
@Transactional
public class AccountDao extends DaoBase<Account, Long> {
    public Account findByEmail(String email) {
        return queryFactory.selectFrom(account)
                .where(account.email.eq(email))
                .fetchOne();
    }

    public Account findByActionToken(String actionToken) {
        return queryFactory.selectFrom(account)
                .where(account.actionToken.eq(actionToken))
                .fetchOne();
    }

    public List<Account> findAllByIds(List<Long> ids) {
        return queryFactory.selectFrom(account)
                .where(account.accountId.in(ids)
                        .and(account.active.isTrue())
                        .and(account.deletedAt.isNull()))
                .fetch();
    }

    public List<Account> findAllBySearchKeyNotInProjectAdministratorsLimitBy(String searchKey, Project project, Long limit) {
        return queryFactory.selectFrom(account)
                .where(account.active.isTrue()
                        .and(account.deletedAt.isNull())
                        .andAnyOf(account.email.containsIgnoreCase(searchKey),
                                account.firstName.containsIgnoreCase(searchKey),
                                account.lastName.containsIgnoreCase(searchKey))
                        .and(account.administeredProjects.contains(project).not()))
                .limit(limit)
                .fetch();
    }

    public List<Account> findAllBySearchKeyNotInProjectParticipantsLimitBy(String searchKey, Project project, Long limit) {
        return queryFactory.selectFrom(account)
                .where(account.active.isTrue()
                        .and(account.deletedAt.isNull())
                        .andAnyOf(account.email.containsIgnoreCase(searchKey),
                                account.firstName.containsIgnoreCase(searchKey),
                                account.lastName.containsIgnoreCase(searchKey))
                        .and(account.participatedProjects.contains(project).not()))
                .limit(limit)
                .fetch();
    }

    public List<Account> findAllBySearchKeyLimitBy(String searchKey, Long limit) {
        return queryFactory.selectFrom(account)
                .where(account.active.isTrue()
                        .and(account.deletedAt.isNull())
                        .andAnyOf(account.email.containsIgnoreCase(searchKey),
                                account.firstName.containsIgnoreCase(searchKey),
                                account.lastName.containsIgnoreCase(searchKey)))
                .limit(limit)
                .fetch();
    }

    @Override
    public void persist(Account entity) {
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

    @Override
    public void delete(Account entity) {
        entity.setDeletedAt(new Date());
    }
}
