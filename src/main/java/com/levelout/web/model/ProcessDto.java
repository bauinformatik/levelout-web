package com.levelout.web.model;

import com.levelout.web.enums.ProcessStatusType;
import com.levelout.web.enums.ProcessType;

public class ProcessDto {
    long projectId;
    long topicId;
    long deserializerOid;
    ProcessType processType;
    ProcessStatusType processStatusType;
    int percentage=0;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public long getDeserializerOid() {
        return deserializerOid;
    }

    public void setDeserializerOid(long deserializerOid) {
        this.deserializerOid = deserializerOid;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public ProcessStatusType getProcessStatusType() {
        return processStatusType;
    }

    public void setProcessStatusType(ProcessStatusType processStatusType) {
        this.processStatusType = processStatusType;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
