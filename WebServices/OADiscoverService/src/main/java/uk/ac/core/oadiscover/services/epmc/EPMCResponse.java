
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class EPMCResponse {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("hitCount")
    @Expose
    private Long hitCount;
    @SerializedName("nextCursorMark")
    @Expose
    private String nextCursorMark;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("resultList")
    @Expose
    private ResultList resultList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getHitCount() {
        return hitCount;
    }

    public void setHitCount(Long hitCount) {
        this.hitCount = hitCount;
    }

    public String getNextCursorMark() {
        return nextCursorMark;
    }

    public void setNextCursorMark(String nextCursorMark) {
        this.nextCursorMark = nextCursorMark;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public ResultList getResultList() {
        return resultList;
    }

    public void setResultList(ResultList resultList) {
        this.resultList = resultList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EPMCResponse that = (EPMCResponse) o;

        if (!Objects.equals(version, that.version)) return false;
        if (!Objects.equals(hitCount, that.hitCount)) return false;
        if (!Objects.equals(nextCursorMark, that.nextCursorMark))
            return false;
        if (!Objects.equals(request, that.request)) return false;
        return Objects.equals(resultList, that.resultList);
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (hitCount != null ? hitCount.hashCode() : 0);
        result = 31 * result + (nextCursorMark != null ? nextCursorMark.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (resultList != null ? resultList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EPMCResponse{" +
                "version='" + version + '\'' +
                ", hitCount=" + hitCount +
                ", nextCursorMark='" + nextCursorMark + '\'' +
                ", request=" + request +
                ", resultList=" + resultList +
                '}';
    }
}
