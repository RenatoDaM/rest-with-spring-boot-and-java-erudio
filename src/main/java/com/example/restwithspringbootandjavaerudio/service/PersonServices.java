package com.example.restwithspringbootandjavaerudio.service;

import java.util.List;
import java.util.logging.Logger;

import com.example.restwithspringbootandjavaerudio.exceptions.ResourceNotFoundException;
import com.example.restwithspringbootandjavaerudio.mapper.DozerMapper;
import com.example.restwithspringbootandjavaerudio.mapper.custom.PersonMapper;
import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.repository.PersonRepository;
import com.example.restwithspringbootandjavaerudio.vo.v1.PersonVO;
import com.example.restwithspringbootandjavaerudio.vo.v2.PersonVOV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServices {
	
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	PersonRepository repository;

	@Autowired
	PersonMapper mapper;

	public List<PersonVO> findAll() {

		logger.info("Finding all people!");

		return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
	}

	public PersonVO findById(Long id) {
		
		logger.info("Finding one person!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		return DozerMapper.parseObject(entity, PersonVO.class);
	}
	
	public PersonVO create(PersonVO person) {

		logger.info("Creating one person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo =  DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}

	public PersonVOV2 createV2(PersonVOV2 person) {

		logger.info("Creating one person V2!");
		Person entity = new Person();
		entity = mapper.convertVoToEntity(person);
		var vo =   mapper.convertEntityToVo(repository.save(entity));
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getId())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one person!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
