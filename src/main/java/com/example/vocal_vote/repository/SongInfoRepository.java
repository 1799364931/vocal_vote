package com.example.vocal_vote.repository;

import com.example.vocal_vote.pojo.SongInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongInfoRepository extends JpaRepository<SongInfo,Integer> {

}
