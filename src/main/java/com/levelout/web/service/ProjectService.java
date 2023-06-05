package com.levelout.web.service;

import com.levelout.web.config.BimServerClientWrapper;
import com.levelout.web.enums.IfcSchema;
import com.levelout.web.model.ProjectModel;
import com.levelout.web.model.RevisionModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.interfaces.objects.*;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by akiajk on 8/1/2017.
 */
@Service
public class ProjectService {
	final static Log logger = LogFactory.getLog(ProjectService.class);

	@Value("#{'${bimserver.plugin.defaultWriteExtendedData}'.split(',')}")
	private List<String> schemaNames;

	@Autowired
	BimServerClientWrapper bimServerClient;

	@Autowired
	PluginService pluginService;

	@Value("${bimserver.plugin.key}")
	private String pluginKey;

	public void createProject(ProjectModel projectModel) throws ServerException, UserException {
		SProject project = bimServerClient.getServiceInterface().addProject(
				projectModel.getName(),
				projectModel.getSchema().name()
		);
		projectModel.setProjectId(project.getOid());
		updateProjectDetails(projectModel, project);

		pluginService.addLevelOutServiceToProject(project);
	}

	public void updateProject(ProjectModel projectModel) throws ServerException, UserException {
		SProject project = bimServerClient.getServiceInterface().getProjectByPoid(projectModel.getProjectId());
		updateProjectDetails(projectModel, project);
	}

	private void updateProjectDetails(ProjectModel projectModel, SProject project) throws ServerException, UserException {
		project.setName(projectModel.getName());
		project.setSchema(projectModel.getSchema().name());
		project.setExportLengthMeasurePrefix(projectModel.getExportLengthMeasurePrefix());
		project.setDescription(projectModel.getDescription());
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

	public ProjectModel getProjectById(long projectId) throws ServerException, UserException {
		SProject sProject = bimServerClient.getServiceInterface().getProjectByPoid(projectId);
		return mapSProjectToProject(sProject);
	}

	public List<ProjectModel> getProjectsByName(String projectName) throws ServerException, UserException {
		List<SProject> projects = bimServerClient.getServiceInterface().getProjectsByName(projectName);
		return projects.stream().map(this::mapSProjectToProject).collect(Collectors.toList());
	}

	public List<ProjectModel> getAllProjects() throws ServerException, UserException {
		List<SProject> projects = bimServerClient.getServiceInterface().getAllProjects(true, true);
		return projects.stream().map(this::mapSProjectToProject).collect(Collectors.toList());
	}

	public List<RevisionModel> getAllRevisions(long projectId) throws ServerException, UserException {
		List<SRevision> revisions = bimServerClient.getServiceInterface().getAllRevisionsOfProject(projectId);
		return revisions.stream().map(this::mapSRevisionToRevision).collect(Collectors.toList());
	}

	private RevisionModel mapSRevisionToRevision(SRevision sRevision) {
		RevisionModel revisionModel = new RevisionModel();
		revisionModel.setRevisionId(sRevision.getOid());
		revisionModel.setDescription(sRevision.getComment());

		List<SExtendedData> services = schemaNames.stream().map(schemaName-> {
			try {
				return bimServerClient.getServiceInterface().getLastExtendedDataOfRevisionAndSchema(
						sRevision.getOid(),
						bimServerClient.getServiceInterface().getExtendedDataSchemaByName(schemaName).getOid()
				);
			} catch (ServerException e) {
				logger.info("Could not set reports due to service exception for revision: " + sRevision.getOid());
				e.printStackTrace();
			} catch (UserException e) {
				logger.info("Could not set reports due to user exception for revision: " + sRevision.getOid());
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
		revisionModel.setReports(
				services.stream()
						.filter(extendedData -> extendedData!=null)
//							.filter(extendedData -> !extendedData.getTitle().toLowerCase().contains("geometry"))
						.collect(
								Collectors.toMap(
										extendedData -> extendedData.getFileId(),
										extendedData -> extendedData.getTitle()
								)
						)
		);
		return revisionModel;
	}

	private ProjectModel mapSProjectToProject(SProject sProject) {
		ProjectModel project = new ProjectModel();
		project.setProjectId(sProject.getOid());
		project.setDescription(sProject.getDescription());
		project.setName(sProject.getName());
		project.setSchema(IfcSchema.valueOf(sProject.getSchema()));
		project.setExportLengthMeasurePrefix(sProject.getExportLengthMeasurePrefix());
		return project;
	}

	/**
	 * Cleans all the in progress topics at midnight CET time everyday
	 *
	 * @throws ServerException
	 * @throws UserException
	 */
	@Scheduled(cron = "0 0 0 * * *", zone = "CET")
	public void topicsCleanupAction() throws ServerException, UserException {
//		bimServerClient.getRegistry().getProgressTopicsOnServer().forEach(topic-> {
//			logger.error("Cleaning up started for topic: "+topic);
//			try {
//				bimServerClient.getServiceInterface().cleanupLongAction(topic);
//				logger.error("Cleaning up successful for topic: "+topic);
//			} catch (UserException | ServerException e) {
//				e.printStackTrace();
//			}
//		});
	}

	public SFile getReportData(long reportId) throws ServerException, UserException {
		return bimServerClient.getServiceInterface().getFile(reportId);
	}
}
