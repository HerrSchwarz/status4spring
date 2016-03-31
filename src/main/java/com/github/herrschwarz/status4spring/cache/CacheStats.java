package com.github.herrschwarz.status4spring.cache;

public class CacheStats {

    private String name;
    private String type;
    private Integer numberOfHits;
    private Integer numberOfEntries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfHits() {
        return numberOfHits;
    }

    /**
     * @param numberOfHits: null will be displayed as unkown
     */
    public void setNumberOfHits(Integer numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    /**
     * @param numberOfEntries null will be displayed as unkown
     */
    public void setNumberOfEntries(Integer numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
