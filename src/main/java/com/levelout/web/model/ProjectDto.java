package com.levelout.web.model;

import com.levelout.web.enums.IfcSchema;
import org.bimserver.interfaces.objects.SSIPrefix;

public class ProjectDto {
	long projectId;
	String name;
	String description;
	SSIPrefix exportLengthMeasurePrefix;
	IfcSchema schema;

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SSIPrefix getExportLengthMeasurePrefix() {
		return exportLengthMeasurePrefix;
	}

	public void setExportLengthMeasurePrefix(SSIPrefix exportLengthMeasurePrefix) {
		this.exportLengthMeasurePrefix = exportLengthMeasurePrefix;
	}

	public IfcSchema getSchema() {
		return schema;
	}

	public void setSchema(IfcSchema schema) {
		this.schema = schema;
	}
}
