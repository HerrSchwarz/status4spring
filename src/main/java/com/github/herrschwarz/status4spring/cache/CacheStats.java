package com.github.herrschwarz.status4spring.cache;

public class CacheStats {

    private String name;
    private String type;
    private Long numberOfHits;
    private Long numberOfEntries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumberOfHits() {
        return numberOfHits;
    }

    /**
     * @param numberOfHits: null will be displayed as unkown
     */
    public void setNumberOfHits(Long numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public Long getNumberOfEntries() {
        return numberOfEntries;
    }

    /**
     * @param numberOfEntries null will be displayed as unkown
     */
    public void setNumberOfEntries(Long numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
