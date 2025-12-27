package com.wic.edu.kg.enums;

import lombok.Getter;

/**
 * 图片库分类枚举
 */
@Getter
public enum GalleryCategory {
    ALL("all", "全部", "All"),
    CAMPUS("campus", "校园风光", "Campus"),
    ACTIVITY("activity", "活动精彩", "Activities"),
    LANDSCAPE("landscape", "自然风景", "Landscape"),
    LIFE("life", "校园生活", "Campus Life"),
    OTHER("other", "其他", "Other");

    private final String code;
    private final String nameZh;
    private final String nameEn;

    GalleryCategory(String code, String nameZh, String nameEn) {
        this.code = code;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
    }

    /**
     * 根据code获取枚举
     */
    public static GalleryCategory fromCode(String code) {
        if (code == null)
            return null;
        for (GalleryCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 判断是否为有效分类code
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
