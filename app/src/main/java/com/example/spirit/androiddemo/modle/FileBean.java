package com.example.spirit.androiddemo.modle;

import java.io.File;

public class FileBean {
    private File file;
    private String type;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "file=" + file +
                ", type='" + type + '\'' +
                '}';
    }
}
