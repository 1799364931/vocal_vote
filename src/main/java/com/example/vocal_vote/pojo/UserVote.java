package com.example.vocal_vote.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tb_user_votes")
public class UserVote {

    @EmbeddedId
    private UserVoteId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("songId")
    @JoinColumn(name = "song_id")
    private SongInfo song;

    @Column(name = "vote_rank")
    private Integer rank;
}
