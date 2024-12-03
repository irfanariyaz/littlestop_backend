package com.shopr.catalog.service.user;

import com.shopr.catalog.model.User;
import com.shopr.catalog.repo.UserRepository;
import com.shopr.catalog.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    @Override
    public User login(LoginRequest request) {
       User user = userRepository.findByEmailAndPassword(request.getEmail(),request.getPassword());
       if(user == null){
           throw new RuntimeException("Invalid email or password");
       }else {
           return user;
       }
    }
}
