package com.example.restwithspringbootandjavaerudio.serialization.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

public class YamlToHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlToHttpMessageConverter() {
        super(new YAMLMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL),
                MediaType.parseMediaType("application/x-yaml")
        );
    }
}
