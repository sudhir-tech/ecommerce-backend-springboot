package com.sudhir.ecommercebackend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sudhir.ecommercebackend.dto.LoginRequestDTO;
import com.sudhir.ecommercebackend.dto.UserResponseDTO;
import com.sudhir.ecommercebackend.entity.User;
import com.sudhir.ecommercebackend.jwt.JwtUtil;
import com.sudhir.ecommercebackend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public UserResponseDTO registerUser(User user) {

    	user.setPassword(passwordEncoder.encode(user.getPassword()));

    	User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        List<UserResponseDTO> responseList = new ArrayList<>();

        for(User user : users) {

            responseList.add(
                    new UserResponseDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail()
                    )
            );
        }

        return responseList;
    }
    public String loginUser(LoginRequestDTO loginDTO) {

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElse(null);

        if(user != null &&
           passwordEncoder.matches(
                   loginDTO.getPassword(),
                   user.getPassword())) {

            return JwtUtil.generateToken(user.getEmail());
        }

        return "Invalid Email or Password";
    }
}