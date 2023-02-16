package com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.person;

import com.example.restwithspringbootandjavaerudio.integrationtests.vo.PersonVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
@XmlRootElement
public class PagedModelPerson implements Serializable {
    private static final long serialVersionUID = 1L;

    // TIVE PROBLEMAS PARA USAR O @XmlElement para mapear o nome no XML, porém usando o @JsonProperty consegui
    // mapear mesmo sendo XML... Deixei o nome da variável como content, assim não seria necessário o mapeamento
    // manual.
    @XmlElement(name = "content")
    List<PersonVO> content;

    public PagedModelPerson() {}

    public List<PersonVO> getContent() {
        return content;
    }

    @XmlElement(name = "content")
    public void setContent(List<PersonVO> content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagedModelPerson that = (PagedModelPerson) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
