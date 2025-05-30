package com.kusitms.website.domain.project.entity;

public enum ProjectType {
    APP("app"),
    WEB("web"),
    CHROME_EXTENSION("chrome extension");

    private String name;

    ProjectType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProjectType from(String name) {
        for(ProjectType type : ProjectType.values()) {
            if(type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }
}
