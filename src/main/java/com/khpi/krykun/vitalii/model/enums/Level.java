package com.khpi.krykun.vitalii.model.enums;

import org.apache.commons.lang3.StringUtils;

public enum Level {

    JUNIOR(1, "Junior"),
    MIDDLE(2, "Middle"),
    SENIOR(3, "Senior");

    private int rank;
    private String value;

    Level(int rank, String value) {
        this.rank = rank;
        this.value = value;
    }

    public int getRank() {
        return rank;
    }

    public String getValue() {
        return value;
    }

    public static Level find(String valueToFind) {
        for (Level levelCandidate : Level.values()) {
            if (StringUtils.equalsIgnoreCase(levelCandidate.getValue(), valueToFind)) {
                return levelCandidate;
            }
        }
        return null;
    }
}
