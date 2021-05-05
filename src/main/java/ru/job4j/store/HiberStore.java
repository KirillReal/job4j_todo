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

    @Override
    public Item create(Item element) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(element);
            session.getTransaction().commit();
        }
        return element;
    }

    @Override
    public boolean update(int id, Item element) {
        boolean result = true;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            element.setId(id);
            if (session.get(Item.class, id) != null) {
                Query query = session.createQuery(
                        "update ru.job4j.model.Item set finished = :finished where id = :id");
                query.setParameter("finished", true);
                query.setParameter("id", id);
                query.executeUpdate();
            } else {
                result = false;
            }
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public boolean delete(int id) {
        boolean result = false;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Item item = new Item(id);
            if (session.get(Item.class, id) != null) {
                session.delete(item);
                result = true;
            }
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public Collection<Item> findAll() {
        List<Item> result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.model.Item").list();
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public Item findById(int id) {
        Item item;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            item = session.get(Item.class, id);
            session.getTransaction().commit();
        }
        return item;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
