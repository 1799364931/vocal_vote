package com.example.vocal_vote.pojo;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity

@Table(name = "tb_user")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id" , unique = true , nullable = false)
    private Integer id;
    @Column(name = "nick_name" ,unique = true,nullable = false)
    private String nickName;
    @ManyToMany
    @JoinTable(
            name = "user_votes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<SongInfo> votedSongs;

}
