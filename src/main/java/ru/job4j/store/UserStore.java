package ru.job4j.store;

import ru.job4j.model.User;

import java.util.Collection;

public interface UserStore {
    User createUser(User user);

    User findByEmailUser(String email);

}
