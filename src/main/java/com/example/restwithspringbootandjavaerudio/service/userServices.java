package com.example.restwithspringbootandjavaerudio.service;

import com.example.restwithspringbootandjavaerudio.controller.PersonController;
import com.example.restwithspringbootandjavaerudio.exceptions.RequiredObjectIsNullNotFoundException;
import com.example.restwithspringbootandjavaerudio.exceptions.ResourceNotFoundException;
import com.example.restwithspringbootandjavaerudio.mapper.DozerMapper;
import com.example.restwithspringbootandjavaerudio.mapper.custom.PersonMapper;
import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.repository.PersonRepository;
import com.example.restwithspringbootandjavaerudio.repository.UserRepository;
import com.example.restwithspringbootandjavaerudio.vo.v1.PersonVO;
import com.example.restwithspringbootandjavaerudio.vo.v2.PersonVOV2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class userServices implements UserDetailsService {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    UserRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public userServices(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by name");
        var user = repository.findByUserName(username);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }
}
