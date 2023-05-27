package com.levelout.web.model;

import com.levelout.web.enums.ProcessStatusType;
import com.levelout.web.enums.ProcessType;
import org.bimserver.interfaces.objects.SActionState;

public class ProcessModel {
    long projectId;
    long revisionId;
    String title;
    long topicId;
    long deserializerOid;
    ProcessType processType;
    ProcessStatusType processStatusType;
    SActionState actionState;
    int percentage=0;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(long revisionId) {
        this.revisionId = revisionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public SActionState getActionState() {
        return actionState;
    }

    public void setActionState(SActionState actionState) {
        this.actionState = actionState;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
