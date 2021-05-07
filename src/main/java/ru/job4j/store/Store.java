package ru.job4j.store;

import ru.job4j.model.Category;
import ru.job4j.model.Item;
import ru.job4j.model.User;

import java.util.Collection;
import java.util.List;

public interface Store {
    Item create(Item element);

    void update(int id, Item element);

    boolean delete(int id);

    Collection<Item> findAll();

    Item findById(int id);

    List<Category> findAllCategory();

    Category findByIdCategory(int id);
}
