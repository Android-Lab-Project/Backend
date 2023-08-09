package com.healthtechbd.backend.security;

import com.healthtechbd.backend.dto.AppUserDetailsDTO;
import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Role;
import com.healthtechbd.backend.entity.Token;
import com.healthtechbd.backend.exception.InvalidTokenException;
import com.healthtechbd.backend.repo.AppUserRepository;
import com.healthtechbd.backend.repo.TokenRepository;
import com.healthtechbd.backend.service.TokenService;
import org.apache.commons.codec.binary.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserServiceSecurity implements UserDetailsService {

    @Autowired
    private final AppUserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private final TokenService tokenService;

    @Autowired
    private PasswordEncoder bcryptPasswordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public AppUserServiceSecurity(AppUserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }


    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(userEmail).
                orElseThrow(() -> new UsernameNotFoundException(("User not found with this email address ")));

            if(user.isAccountVerified())
            {
                return new org.springframework.security.core.userdetails.User(user.getEmail(),
                        user.getPassword(), true, true, true, true, mapRolesToAuthorities(user.getRoles()));
            }

            return null;
    }

    public AppUserDetailsDTO loadAppUserByEmail(String userEmail) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email:" + "okay"));

        if (!user.isAccountVerified()) {
            new ResponseEntity<>("You have to verify First", HttpStatus.OK);
        }

        AppUserDetailsDTO userDetails = modelMapper.map(user,AppUserDetailsDTO.class);
        return userDetails;

    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {

        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().getValue())).collect(Collectors.toList());
    }

    public boolean verifyUser(String token) throws InvalidTokenException {

        Token usertoken = tokenService.findByToken(token);
        if (Objects.isNull(usertoken) || !StringUtils.equals(token, usertoken.getToken()) || usertoken.isExpired()) {
            throw new InvalidTokenException("Token is not valid");
        }
        AppUser user = userRepository.findById(usertoken.getAppUser().getId()).get();
        if (Objects.isNull(user)) {
            return false;
        }
        user.setAccountVerified(true);
        userRepository.save(user);

        tokenService.removeToken(usertoken);
        return true;
    }
}
