package com.levelout.web.model;

import java.io.InputStream;

public class DownloadStreamModel {
    long topicId;
    InputStream inputStream;
    String contentType;
    String extension;

    public DownloadStreamModel(InputStream inputStream, String contentType, String extension, long topicId) {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.extension = extension;
        this.topicId = topicId;
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

    public long getTopicId() {
        return topicId;
    }
}
