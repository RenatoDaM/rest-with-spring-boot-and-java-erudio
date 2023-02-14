package com.example.restwithspringbootandjavaerudio.services;

import java.util.List;
import java.util.logging.Logger;

import com.example.restwithspringbootandjavaerudio.controllers.BookController;
import com.example.restwithspringbootandjavaerudio.data.vo.v1.BookVO;
import com.example.restwithspringbootandjavaerudio.data.vo.v1.PersonVO;
import com.example.restwithspringbootandjavaerudio.exceptions.RequiredObjectIsNullException;
import com.example.restwithspringbootandjavaerudio.exceptions.ResourceNotFoundException;
import com.example.restwithspringbootandjavaerudio.mapper.DozerMapper;
import com.example.restwithspringbootandjavaerudio.model.Book;
import com.example.restwithspringbootandjavaerudio.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;

	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {

		logger.info("Finding all book!");
		var bookPage = repository.findAll(pageable);
		var bookVOPage = bookPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		bookVOPage.map(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		Link link = linkTo(methodOn(BookController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc"))
				.withSelfRel();
		return assembler.toModel(bookVOPage, link);
	}

	public BookVO findById(Long id) {
		
		logger.info("Finding one book!");
		
		var entity = repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		var entity = DozerMapper.parseObject(book, Book.class);
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {

		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity = repository.findById(book.getKey())
			.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
}
