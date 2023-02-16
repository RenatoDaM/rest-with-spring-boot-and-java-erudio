package com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.book;

import com.example.restwithspringbootandjavaerudio.integrationtests.wrappers.person.PersonEmbeddedVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class WrapperBookVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("_embedded")
    private BookEmbeddedVO embedded;

    public WrapperBookVO() {}

    public BookEmbeddedVO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(BookEmbeddedVO embedded) {
        this.embedded = embedded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrapperBookVO that = (WrapperBookVO) o;
        return embedded.equals(that.embedded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(embedded);
    }
}
