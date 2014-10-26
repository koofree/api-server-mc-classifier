package kr.ac.korea.mobide.apiservice.domain.term;

import kr.ac.korea.mobide.apiservice.domain.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Koo Lee on 2014-08-29.
 */
@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue
    private int id;

    @NonNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<User> users;
}
