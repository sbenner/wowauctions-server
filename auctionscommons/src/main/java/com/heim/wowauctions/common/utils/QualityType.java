package com.heim.wowauctions.common.utils;

public enum QualityType {
    POOR(0),
    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(5),
    ARTIFACT(6),
    HEIRLOOM(7);


    int type;

    QualityType(int type) {
        this.type = type;
    }


}
