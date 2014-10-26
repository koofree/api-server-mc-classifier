package kr.ac.korea.mobide.apiservice.domain.user;

import lombok.*;

import javax.persistence.*;

/**
 * Created by Koo Lee on 2014-08-30.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Email {
    @Id
    @NonNull
    private String name;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;
}
