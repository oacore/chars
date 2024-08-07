package uk.ac.core.worker.sitemap.collection.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ESDump {

    @JsonProperty("_source")
    private DataWrapper dataWrapper;

    public int getId() {
        return this.dataWrapper.getId();
    }

    public boolean getTextStatus() {
        return this.dataWrapper.getTextStatus();
    }

    public ESDump() {
    }

    public ESDump(int id, boolean text) {
        this.dataWrapper = new DataWrapper();
        this.dataWrapper.setId(id);
        DataWrapper.StatWrapper statWrapper = new DataWrapper.StatWrapper();
        statWrapper.setText(text);
        this.dataWrapper.setStatWrapper(statWrapper);
    }

    private static class DataWrapper {

        private int id;

        @JsonProperty("repositoryDocument")
        private StatWrapper statWrapper;

        public void setStatWrapper(StatWrapper statWrapper) {
            this.statWrapper = statWrapper;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public boolean getTextStatus() {
            return this.statWrapper.hasText();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataWrapper that = (DataWrapper) o;

            if (id != that.id) return false;
            return Objects.equals(statWrapper, that.statWrapper);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (statWrapper != null ? statWrapper.hashCode() : 0);
            return result;
        }

        private static class StatWrapper {

            @JsonProperty("textStatus")
            private boolean text;

            public void setText(boolean text) {
                this.text = text;
            }

            public boolean hasText() {
                return text;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                StatWrapper that = (StatWrapper) o;

                return text == that.text;
            }

            @Override
            public int hashCode() {
                return (text ? 1 : 0);
            }
        }
    }

    public void setDataWrapper(DataWrapper dataWrapper) {
        this.dataWrapper = dataWrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ESDump esDump = (ESDump) o;

        return Objects.equals(dataWrapper, esDump.dataWrapper);
    }

    @Override
    public int hashCode() {
        return dataWrapper != null ? dataWrapper.hashCode() : 0;
    }
}