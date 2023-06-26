package com.levelout.web.model;

import java.io.InputStream;

public class DownloadStreamModel {
    InputStream inputStream;
    String contentType;
    String extension;

    public DownloadStreamModel(InputStream inputStream, String contentType, String extension) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.extension = extension;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }
}
