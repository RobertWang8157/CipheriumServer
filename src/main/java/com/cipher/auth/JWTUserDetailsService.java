package com.cipher.auth;



import com.cipher.entity.User;
import com.cipher.repostory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authList = new ArrayList<>();

        Optional<User> optOfficer = userRepository.findById(username);
        if(optOfficer.isEmpty()){
            throw new UsernameNotFoundException("Use not found with use Name: " + username);
        }

        List<String> programCodeList = new ArrayList<>();
        User user = optOfficer.get();

        authList = AuthorityUtils.createAuthorityList(programCodeList.toArray(new String[0]));

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authList);
    }
}
