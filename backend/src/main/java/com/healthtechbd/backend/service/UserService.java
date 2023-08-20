package com.healthtechbd.backend.service;

import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Role;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;

    public RegistrationResponse registerUser(SignUpDTO signUpDTO, String roleType) {
        ApiResponse errorResponse = new ApiResponse();

        if (signUpDTO.getFirstName() == null || signUpDTO.getFirstName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "First Name can not be empty");

        if (signUpDTO.getLastName() == null || signUpDTO.getLastName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Last Name can not be empty");

        if (signUpDTO.getEmail() == null || signUpDTO.getEmail().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Email can not be empty");

        if (signUpDTO.getPassword() == null || signUpDTO.getPassword().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Password can not be empty");

        if (userRepository.existsByEmail(signUpDTO.getEmail()))
            errorResponse = ApiResponse.create("error", "Email is already taken!");

        if (!errorResponse.empty()) {
            return new RegistrationResponse(errorResponse, null);
        }

        String password = bcryptPasswordEncoder.encode(signUpDTO.getPassword());
        signUpDTO.setPassword(password);

        AppUser user = modelMapper.map(signUpDTO, AppUser.class);
        user.setAccountVerified(true);

        Role role = new Role(roleType);
        user.setRoles(Collections.singletonList(role));

        return new RegistrationResponse(ApiResponse.create("create", "Sign up successful"), user);
    }
}
