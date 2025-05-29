package com.ntabodoiqua.online_course_management.enums;

import lombok.Getter;

@Getter
public enum DefaultUrl {

    // Default URLs for various resources
    THUMBNAIL("thumbnail", "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"),
    COURSE_THUMBNAIL("course_thumbnail", "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png"),
    ;
    DefaultUrl (String type, String URL) {
        this.type = type;
        this.URL = URL;
    }
    private final String type;
    private final String URL;
}
