package com.example.restwithspringbootandjavaerudio.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class DozerMapper {
    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    public static <O, D> D parseObject(O origin, Class<D> destionation) {
        return mapper.map(origin, destionation);
    }

    public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destionation) {
        List<D> destinationObjects = new ArrayList<>();
        for (O o : origin) {
            destinationObjects.add(mapper.map(o, destionation));
        }
        return destinationObjects;
    }
}
