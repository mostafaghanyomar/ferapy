package com.ferapy.repository;

import com.ferapy.document.UserDO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDO, String> {

    Optional<UserDO> findByUsername(String username);

}
