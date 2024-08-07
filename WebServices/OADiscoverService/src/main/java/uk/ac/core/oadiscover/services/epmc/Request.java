
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Request {

    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("resultType")
    @Expose
    private String resultType;
    @SerializedName("synonym")
    @Expose
    private Boolean synonym;
    @SerializedName("cursorMark")
    @Expose
    private String cursorMark;
    @SerializedName("pageSize")
    @Expose
    private Long pageSize;
    @SerializedName("sort")
    @Expose
    private String sort;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Boolean getSynonym() {
        return synonym;
    }

    public void setSynonym(Boolean synonym) {
        this.synonym = synonym;
    }

    public String getCursorMark() {
        return cursorMark;
    }

    public void setCursorMark(String cursorMark) {
        this.cursorMark = cursorMark;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (resultType != null ? resultType.hashCode() : 0);
        result = 31 * result + (synonym != null ? synonym.hashCode() : 0);
        result = 31 * result + (cursorMark != null ? cursorMark.hashCode() : 0);
        result = 31 * result + (pageSize != null ? pageSize.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (!Objects.equals(query, request.query)) return false;
        if (!Objects.equals(resultType, request.resultType)) return false;
        if (!Objects.equals(synonym, request.synonym)) return false;
        if (!Objects.equals(cursorMark, request.cursorMark)) return false;
        if (!Objects.equals(pageSize, request.pageSize)) return false;
        return Objects.equals(sort, request.sort);
    }
}
