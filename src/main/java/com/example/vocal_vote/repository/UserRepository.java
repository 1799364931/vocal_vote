package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByNickName(String nickName);
}
