package es.codeurjc.daw.bucket.dto;

import java.util.Date;

public class BucketDto {

    private String name;
    private String ownerName;
    private Date creationDate;
    
    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public static final class Builder {

        private final BucketDto object;

        public Builder() {
            object = new BucketDto();
        }

        public Builder withName(String value) {
            object.name = value;
            return this;
        }

        public Builder withOwnerName(String value) {
            object.ownerName = value;
            return this;
        }

        public Builder withCreationDate(Date value) {
            object.creationDate = value;
            return this;
        }

        public BucketDto build() {
            return object;
        }

    }

}
