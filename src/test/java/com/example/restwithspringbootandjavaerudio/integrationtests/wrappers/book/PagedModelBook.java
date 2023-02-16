package com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.book;

import com.example.restwithspringbootandjavaerudio.integrationtests.vo.BookVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class PagedModelBook implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "content")
    List<BookVO> content;

    public PagedModelBook() {}

    public List<BookVO> getContent() {
        return content;
    }

    public void setContent(List<BookVO> content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagedModelBook that = (PagedModelBook) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
