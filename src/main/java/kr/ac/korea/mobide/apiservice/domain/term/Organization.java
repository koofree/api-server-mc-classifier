package kr.ac.korea.mobide.apiservice.domain.term;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Koo Lee on 2014-08-30.
 */
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Organization {

    @Id
    @GeneratedValue
    private int id;

    @NonNull
    private String name;
}
