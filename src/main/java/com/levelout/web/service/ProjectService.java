package com.levelout.web.service;

import com.levelout.web.config.BimServerClientWrapper;
import com.levelout.web.enums.IfcSchema;
import com.levelout.web.model.ProjectDto;
import com.levelout.web.model.RevisionDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SRevision;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by akiajk on 8/1/2017.
 */
@Service
public class ProjectService {
	final static Log logger = LogFactory.getLog(ProjectService.class);
	
	@Autowired
	BimServerClientWrapper bimServerClient;

	@Autowired
	PluginService pluginService;

	public void createProject(ProjectDto projectDto) throws ServerException, UserException {
		SProject project = bimServerClient.getServiceInterface().addProject(
				projectDto.getName(),
				projectDto.getSchema().name()
		);
		projectDto.setProjectId(project.getOid());
		updateProjectDetails(projectDto, project);

		pluginService.addLevelOutServiceToProject(project);
	}

	public void updateProject(ProjectDto projectDto) throws ServerException, UserException {
		SProject project = bimServerClient.getServiceInterface().getProjectByPoid(projectDto.getProjectId());
		updateProjectDetails(projectDto, project);
	}

	private void updateProjectDetails(ProjectDto projectDto, SProject project) throws ServerException, UserException {
		project.setName(projectDto.getName());
		project.setSchema(projectDto.getSchema().name());
		project.setExportLengthMeasurePrefix(projectDto.getExportLengthMeasurePrefix());
		project.setDescription(projectDto.getDescription());
		bimServerClient.getServiceInterface().updateProject(project);
	}

	public boolean deleteProject(long projectId) throws ServerException, UserException {
		return bimServerClient.getServiceInterface().deleteProject(projectId);
	}

	public List<String> deleteProjectsByName(String projectName) throws ServerException, UserException {
		List<SProject> projects = bimServerClient.getServiceInterface().getProjectsByName(projectName);
		List<Long> successIds = new ArrayList<>();
		List<String> failedIds = new ArrayList<>();
		projects.forEach(project-> {
			try {
				this.deleteProject(project.getOid());
				successIds.add(project.getOid());
			} catch (Exception e) {
				failedIds.add(project.getOid() + ": " + e.getMessage());
			}
		});
		logger.info("Successful Ids: " + successIds);
		return failedIds;
	}

	public ProjectDto getProjectById(long projectId) throws ServerException, UserException {
		SProject sProject = bimServerClient.getServiceInterface().getProjectByPoid(projectId);
		return mapSProjectToProject(sProject);
	}

	public List<ProjectDto> getProjectsByName(String projectName) throws ServerException, UserException {
		List<SProject> projects = bimServerClient.getServiceInterface().getProjectsByName(projectName);
		return projects.stream().map(this::mapSProjectToProject).collect(Collectors.toList());
	}

	public List<ProjectDto> getAllProjects() throws ServerException, UserException {
		List<SProject> projects = bimServerClient.getServiceInterface().getAllProjects(true, true);
		return projects.stream().map(this::mapSProjectToProject).collect(Collectors.toList());
	}

	public List<RevisionDto> getAllRevisions(long projectId) throws ServerException, UserException {
		List<SRevision> revisions = bimServerClient.getServiceInterface().getAllRevisionsOfProject(projectId);
		return revisions.stream().map(this::mapSRevisionToRevision).collect(Collectors.toList());
	}

	private RevisionDto mapSRevisionToRevision(SRevision sRevision) {
		RevisionDto revisionDto = new RevisionDto();
		revisionDto.setRevisionId(sRevision.getOid());
		revisionDto.setDescription(sRevision.getComment());
		return revisionDto;
	}

	private ProjectDto mapSProjectToProject(SProject sProject) {
		ProjectDto project = new ProjectDto();
		project.setProjectId(sProject.getOid());
		project.setDescription(sProject.getDescription());
		project.setName(sProject.getName());
		project.setSchema(IfcSchema.valueOf(sProject.getSchema()));
		project.setExportLengthMeasurePrefix(sProject.getExportLengthMeasurePrefix());
		return project;
	}

	@Scheduled(cron = "0 0 0 * * *", zone = "CET")
	public void topicsCleanupAction() throws ServerException, UserException {
		bimServerClient.getRegistry().getProgressTopicsOnServer().forEach(topic-> {
			logger.error("Cleaning up started for topic: "+topic);
			try {
				bimServerClient.getServiceInterface().cleanupLongAction(topic);
				logger.error("Cleaning up successful for topic: "+topic);
			} catch (UserException | ServerException e) {
				e.printStackTrace();
			}
		});
	}
}
