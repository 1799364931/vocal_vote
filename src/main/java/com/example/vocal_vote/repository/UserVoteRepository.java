package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.UserVote;
import com.example.vocal_vote.pojo.UserVoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoteRepository extends JpaRepository<UserVote,UserVoteId> {
}
