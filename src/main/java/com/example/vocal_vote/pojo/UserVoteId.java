package com.example.vocal_vote.pojo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Embeddable
@NoArgsConstructor
public class UserVoteId implements Serializable {
    private Integer userId;
    private Integer songId;

    // equals() 和 hashCode() 必须重写
}
