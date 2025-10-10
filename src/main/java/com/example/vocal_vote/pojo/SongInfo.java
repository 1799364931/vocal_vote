package com.example.vocal_vote.pojo;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_songinfo",
    indexes = {
        @Index(name = "idx_year",columnList = "year"),
        @Index(name = "idx_game",columnList = "game_name")
    })

public class SongInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id" , unique = true , nullable = false)
    private Integer id;
    @Column(name = "game_name" ,nullable = false)
    private String gameName;
    @Column(name = "song_name" ,nullable = false)
    private String songName;
    @Column(name = "score",nullable = false)
    private Integer score;
    @Column(name = "vote_count",nullable = false)
    private Integer vote_count;
    @Column(name = "year",nullable = false)
    private Integer year;
}
