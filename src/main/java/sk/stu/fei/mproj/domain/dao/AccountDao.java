package sk.stu.fei.mproj.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.domain.entities.Account;

import java.util.Date;

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
