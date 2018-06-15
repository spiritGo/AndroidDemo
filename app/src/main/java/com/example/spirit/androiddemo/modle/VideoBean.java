package com.example.spirit.androiddemo.modle;

public class VideoBean {
    private String name;
    private String path;
    private String duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
