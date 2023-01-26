package com.example.restwithspringbootandjavaerudio.mapper;

import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.vo.v1.PersonVO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperTest {
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.createTypeMap(PersonVO.class, Person.class)
                .<Long>addMapping(src -> src.getKey(), (dest,value) -> dest.setId(value));

        modelMapper.createTypeMap( Person.class, PersonVO.class)
                .<Long>addMapping(src -> src.getId(), (dest,value) -> dest.setKey(value));
        return modelMapper;
    }

}
