package ru.job4j.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.model.User;

import java.util.function.Function;

public class UserServStore implements UserStore, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HiberStore.class.getName());
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }


    private static final class Lazy {
        private static final UserStore INST = new UserServStore();
    }

    public static UserStore instOf() {
        return Lazy.INST;
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            LOG.error(e.getMessage(), e);

            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public User findByEmailUser(String email) {
        return this.tx(
                session -> session.createQuery(
                        "from ru.job4j.model.User where email = :email", User.class)
                        .setParameter("email", email)
                        .uniqueResult());
    }

    @Override
    public User createUser(User user) {
        return tx(session -> {
            session.save(user);
            return user;
        });
    }

}
