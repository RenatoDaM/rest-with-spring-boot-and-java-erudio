package com.example.restwithspringbootandjavaerudio.services;

import com.example.restwithspringbootandjavaerudio.data.vo.v1.security.AccountCredentialsVO;
import com.example.restwithspringbootandjavaerudio.data.vo.v1.security.TokenVO;
import com.example.restwithspringbootandjavaerudio.repositories.UserRepository;
import com.example.restwithspringbootandjavaerudio.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AuthServices {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired
	private UserRepository repository;
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialsVO data) {
		try {
			var username = data.getUsername();
			var password = data.getPassword();
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
			
			var user = repository.findByUsername(username);
			System.out.println(user.getId());
			System.out.println(user.getPassword());
			System.out.println(user.getUserName());
			var tokenResponse = new TokenVO();
			if (user != null) {
				System.out.println("nao ta nullllll");
				tokenResponse = tokenProvider.createAccessToken(username, user.getRoles());
			} else {
				System.out.println("userrrrrrrr not found");;
			}
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			System.out.println("bad cred");
			throw new BadCredentialsException("Invalid username/password supplied!");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {
		var user = repository.findByUsername(username);
		
		var tokenResponse = new TokenVO();
		if (user != null) {
			tokenResponse = tokenProvider.refreshToken(refreshToken);
		} else {
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}
		return ResponseEntity.ok(tokenResponse);
	}
}
