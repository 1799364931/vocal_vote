package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

}
