package kr.ac.korea.mobide.apiservice.interfaces.controller;

import kr.ac.korea.mobide.apiservice.domain.term.Team;
import kr.ac.korea.mobide.apiservice.domain.term.TeamRepository;
import kr.ac.korea.mobide.apiservice.domain.user.User;
import kr.ac.korea.mobide.apiservice.domain.user.UserRepository;
import kr.ac.korea.mobide.apiservice.interfaces.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Koo Lee on 2014-08-29.
 */

@RestController
@Transactional
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/team", method = RequestMethod.POST)
    public Team newTeam(
            @RequestParam String name) {
        Team team = new Team(name);
        teamRepository.save(team);
        return team;
    }

    @RequestMapping(value = "/team/{id}", method = RequestMethod.PUT)
    public Team fixTeam(
            @PathVariable int id,
            @RequestParam String name) {
        Team team = teamRepository.findOne(id);
        team.setName(name);
        return team;
    }

    @RequestMapping(value = "/team/{id}/{user}", method = {RequestMethod.POST, RequestMethod.PUT})
    public Team addUser(@PathVariable int id,
                        @PathVariable(value = "user") String userId) {
        Team team = teamRepository.findOne(id);
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new ObjectNotFoundException();
        }

        team.getUsers().add(user);
        return team;
    }

    @RequestMapping(value = "/team", method = RequestMethod.GET)
    public List<Team> list() {
        return teamRepository.findAll();
    }
}
