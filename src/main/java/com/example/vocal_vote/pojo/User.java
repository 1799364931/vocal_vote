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

    @OneToMany(mappedBy = "user")
    private List<UserVote> votes;


}
