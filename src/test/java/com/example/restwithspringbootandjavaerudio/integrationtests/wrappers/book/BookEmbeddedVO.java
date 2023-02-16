package com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.book;

import com.example.restwithspringbootandjavaerudio.integrationtests.vo.BookVO;
import com.example.restwithspringbootandjavaerudio.integrationtests.vo.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BookEmbeddedVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("bookVOList")
    private List<BookVO> books;

    public BookEmbeddedVO() {}

    public List<BookVO> getBooks() {
        return books;
    }

    public void setBooks(List<BookVO> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEmbeddedVO that = (BookEmbeddedVO) o;
        return books.equals(that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(books);
    }
}
