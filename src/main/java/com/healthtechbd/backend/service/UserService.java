package com.healthtechbd.backend.service;

import com.healthtechbd.backend.dto.SignUpDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Role;
import com.healthtechbd.backend.entity.UserCountStats;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.UserCountStatsRepository;
import com.healthtechbd.backend.utils.ApiResponse;
import com.healthtechbd.backend.utils.RegistrationResponse;
import com.healthtechbd.backend.utils.UpdateUserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private UserCountStatsRepository userCountStatsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;

    public AppUser returnUser(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("username");

        Optional<AppUser> optionalAppUser = userRepository.findByEmail(userEmail);

        AppUser user = new AppUser();

        if (optionalAppUser.isPresent()) {
            user = optionalAppUser.get();
            return user;
        } else {
            return null;
        }
    }

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

    public void AddUserCount(LocalDate date) {
        Optional<UserCountStats> optionalUserCountStats = userCountStatsRepository.findByDate(date);

        UserCountStats userCountStats;

        if (!optionalUserCountStats.isPresent()) {
            userCountStats = new UserCountStats();

            userCountStats.setCount(1L);
            userCountStats.setDate(date);
        } else {
            userCountStats = optionalUserCountStats.get();
            userCountStats.setCount(userCountStats.getCount() + 1);
        }

        userCountStatsRepository.save(userCountStats);
    }

    public UpdateUserResponse updateUser(SignUpDTO signUpDTO) {
        ApiResponse errorResponse = new ApiResponse();

        if (signUpDTO.getFirstName() == null || signUpDTO.getFirstName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "First Name can not be empty");

        if (signUpDTO.getLastName() == null || signUpDTO.getLastName().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Last Name can not be empty");

        if (signUpDTO.getEmail() == null || signUpDTO.getEmail().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Email can not be empty");

        if (signUpDTO.getPassword() == null || signUpDTO.getPassword().trim().length() == 0)
            errorResponse = ApiResponse.create("error", "Password can not be empty");

        if (!errorResponse.empty()) {
            return new UpdateUserResponse(errorResponse, null);
        }

        String password = bcryptPasswordEncoder.encode(signUpDTO.getPassword());
        signUpDTO.setPassword(password);

        AppUser user = modelMapper.map(signUpDTO, AppUser.class);
        user.setAccountVerified(true);

        return new UpdateUserResponse(ApiResponse.create("update", "User updated "), user);
    }


}
