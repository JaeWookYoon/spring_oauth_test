package com.jwyoon.oauth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwyoon.oauth.model.UserAuth;
import com.jwyoon.oauth.model.UserList;
import com.jwyoon.oauth.oauth.PasswordEncoders;
import com.jwyoon.oauth.repository.UserListRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserListRepository userListRepository;
	
	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		UserList user = userListRepository.findUserById(username);
		
		if(user != null) {			
			List<UserAuth> auth = userListRepository.findUserAuthById(user.getUserId());
			List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
			for(UserAuth a:auth) {
				 System.out.println("AUTH = " + a.getId() + " " + a.getAuth());
				 GrantedAuthority authority = new SimpleGrantedAuthority(a.getAuth());				 
				 grantList.add(authority);				 
			}
			 
			User userDetails = new User(user.getUserId(),user.getUserPassword(),grantList);
            return userDetails;
		}else {			
			return null;
		}
	}
}
