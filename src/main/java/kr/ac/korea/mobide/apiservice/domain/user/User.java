package kr.ac.korea.mobide.apiservice.domain.user;

import com.google.gson.Gson;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Koo Lee on 2014-08-29.
 */
@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private int id;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    private List<Email> emails;

    private String name = "";

    @NonNull
    private String password;

    private String roles = "";

    private boolean expired = false;

    private boolean locked = false;

    private boolean credentialsExpired = false;

    private boolean enabled = true;


    public Set<Role> getRoles() {
        Set<Role> set = new Gson().fromJson(roles, Set.class);
        if (set == null)
            set = new HashSet<>();
        return set;
    }

    private Set<String> getRoleList() {
        Set<String> roleList = new Gson().fromJson(roles, Set.class);
        if (roleList == null)
            roleList = new HashSet<>();
        return roleList;
    }

    public User addRole(Role role) {
        Set<String> roleList = getRoleList();
        roleList.add(role.toString());
        roles = new Gson().toJson(roleList);
        return this;
    }

    public User removeRole(Role role) {
        Set<String> roleList = getRoleList();
        roleList.remove(role.toString());
        roles = new Gson().toJson(roleList);
        return this;
    }
}
