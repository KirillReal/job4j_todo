package ru.job4j.store;

import ru.job4j.model.Item;

import java.util.Collection;

public interface Store {
    Item create(Item element);

    void update(int id, Item element);

    boolean delete(int id);

    Collection<Item> findAll();

    Item findById(int id);
}
