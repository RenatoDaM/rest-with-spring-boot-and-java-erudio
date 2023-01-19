package com.example.restwithspringbootandjavaerudio.mapper.custom;

import com.example.restwithspringbootandjavaerudio.model.Person;
import com.example.restwithspringbootandjavaerudio.vo.v2.PersonVOV2;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {
    public PersonVOV2 convertEntityToVo(Person person) {
        PersonVOV2 vov2 = new PersonVOV2();
        vov2.setId(person.getId());
        vov2.setAddress(person.getAddress());
        vov2.setGender(person.getGender());
        vov2.setFirstName(person.getFirstName());
        vov2.setLastName(person.getLastName());
        vov2.setBirthDay(new Date());
        return vov2;
    }

    public Person convertVoToEntity(PersonVOV2 vov2) {
        Person person = new Person();
        person.setId(vov2.getId());
        person.setAddress(vov2.getAddress());
        person.setGender(vov2.getGender());
        person.setFirstName(vov2.getFirstName());
        person.setLastName(vov2.getLastName());
        return person;
    }
}
