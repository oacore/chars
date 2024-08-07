
package uk.ac.core.oadiscover.services.epmc;

import java.util.List;
import java.util.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorList {

    @SerializedName("author")
    @Expose
    private List<Author> author = null;

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorList that = (AuthorList) o;

        return Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return author != null ? author.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AuthorList{" +
                "author=" + author +
                '}';
    }
}
