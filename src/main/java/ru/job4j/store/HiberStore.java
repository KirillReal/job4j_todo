package ru.job4j.store;

import ru.job4j.model.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class HiberStore implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(HiberStore.class.getName());
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    private static final class Lazy {
        private static final Store INST = new HiberStore();
    }

    public static Store instOf() {
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
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Item create(Item element) {
        this.tx(
                session -> session.save(element));
        return element;
    }

    @Override
    public void update(int id, Item element) {
        this.tx(session -> session.createQuery(
                "update ru.job4j.model.Item set done = :done where id = :id")
                .setParameter("done", true)
                .setParameter("id", id)
                .executeUpdate()
        );
    }

    @Override
    public boolean delete(int id) {
        return tx(session -> {
            session.delete(id);
            return true;
        });
    }

    @Override
    public Collection<Item> findAll() {
        return this.tx(session ->
                session.createQuery("from ru.job4j.model.Item").list());
    }

    @Override
    public Item findById(int id) {
        return this.tx(session -> session.get(Item.class, id));
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
