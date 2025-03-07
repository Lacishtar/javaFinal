package com.tdtu.JavaFn.Interface;

import com.tdtu.JavaFn.Classes.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    User findByToken(String token);
    User findByEmail(String email);
}
