package com.levelout.web.model;

import java.util.Date;
import java.util.Map;

public class RevisionModel {
	long revisionId;
	String description;
	Date date;

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	String extension;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	Map<Long, String> reports;

	public Map<Long, String> getReports() {
		return reports;
	}

	public void setReports(Map<Long, String> reports) {
		this.reports = reports;
	}

	String map;

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public long getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(long revisionId) {
		this.revisionId = revisionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
