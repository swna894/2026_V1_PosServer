package com.swna.server.user.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.swna.server.common.service.AbstractBaseService;
import com.swna.server.user.entity.model.Role;
import com.swna.server.user.entity.model.User;
import com.swna.server.user.infrastructure.repository.UserRepository;
import com.swna.server.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractBaseService<User, Long>{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    public void signup(String username, String password) {

        String encoded = passwordEncoder.encode(password);
        User user = new User(username, encoded, Role.USER);
        userRepository.save(user);
    }

    public void someLogic() {

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

      Long userId = principal.getUserId();
   }
}
