package com.example.restwithspringbootandjavaerudio.service;

import java.util.List;
import java.util.logging.Logger;

import com.example.restwithspringbootandjavaerudio.controller.PersonController;
import com.example.restwithspringbootandjavaerudio.exceptions.RequiredObjectIsNullNotFoundException;
import com.example.restwithspringbootandjavaerudio.exceptions.ResourceNotFoundException;
import com.example.restwithspringbootandjavaerudio.mapper.DozerMapper;
import com.example.restwithspringbootandjavaerudio.mapper.custom.PersonMapper;
import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.repository.PersonRepository;
import com.example.restwithspringbootandjavaerudio.vo.v1.PersonVO;
import com.example.restwithspringbootandjavaerudio.vo.v2.PersonVOV2;
import org.apache.catalina.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.Destination;

@Service
public class PersonServices {
	
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	PersonRepository repository;

	@Autowired
	PersonMapper mapper;
	@Autowired
	private ModelMapper modelMapper;

	public List<PersonVO> findAll() {

		logger.info("Finding all people!");

		var persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons.stream().forEach(person -> person.add(linkTo(methodOn(PersonController.class)
				.findById(person.getKey()))
				.withSelfRel()));
		return persons;
	}

	public PersonVO findById(Long id) {
		
		logger.info("Finding one person!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}

	public PersonVO create(PersonVO person) {

		if (person == null) throw new RequiredObjectIsNullNotFoundException();
		logger.info("Creating one person!");
		var entity = modelMapper.map(person, Person.class);
		System.out.println(entity.getId());
		System.out.println(entity.getFirstName());
		System.out.println(entity.getLastName());
		var vo =  modelMapper.map(entity, PersonVO.class);
		repository.save(entity);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	public PersonVOV2 createV2(PersonVOV2 person) {

		ModelMapper modelMapper = new ModelMapper();
		System.out.println(person.getFirstName());
		System.out.println(person.getLastName());
		Person entity = modelMapper.map(person, Person.class);
		System.out.println(entity.getFirstName());
		System.out.println(entity.getLastName());
		var vo =  modelMapper.map(repository.save(entity), PersonVOV2.class);


		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one person!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
