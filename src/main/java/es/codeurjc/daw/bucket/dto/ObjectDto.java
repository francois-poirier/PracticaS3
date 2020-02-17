package es.codeurjc.daw.bucket.dto;

import java.util.Date;

public class ObjectDto {
    private String eTag;
    private long size;
    private Date lastModified;
    public String getETag() {
        return eTag;
    }

    public long getSize() {
        return size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public static final class Builder {

        private final ObjectDto object;

        public Builder() {
            object = new ObjectDto();
        }

        public Builder withETag(String value) {
            object.eTag = value;
            return this;
        }

        public Builder withSize(long value) {
            object.size = value;
            return this;
        }

        public Builder withLastModified(Date value) {
            object.lastModified = value;
            return this;
        }

        public ObjectDto build() {
            return object;
        }

    }

}
