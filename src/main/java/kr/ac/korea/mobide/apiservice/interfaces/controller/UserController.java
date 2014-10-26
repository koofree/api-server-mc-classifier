package kr.ac.korea.mobide.apiservice.interfaces.controller;

import kr.ac.korea.mobide.apiservice.domain.user.Email;
import kr.ac.korea.mobide.apiservice.domain.user.User;
import kr.ac.korea.mobide.apiservice.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

/**
 * Created by Koo Lee on 2014-08-29.
 */
@RestController
@Transactional
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/user/{id}", method = {RequestMethod.PUT})
    public User fixUser(@PathVariable String id
            , @RequestParam String name) {
        User user = userRepository.findOne(id);

        user.setName(name);

        return user;
    }

    @RequestMapping(value = "/user", method = {RequestMethod.POST})
    public User newUser(
            @RequestParam String email,
            @RequestParam String password) {
        User user = new User(password);
        user.getEmails().add(new Email(email));
        userRepository.save(user);

        return user;
    }
}
