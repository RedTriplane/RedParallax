
package com.jfixby.r3.parallax.desktop;

import java.io.IOException;

import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Set;
import com.jfixby.scarabei.api.desktop.ScarabeiDesktop;
import com.jfixby.scarabei.api.file.File;
import com.jfixby.scarabei.api.file.LocalFileSystem;
import com.jfixby.tool.eclipse.dep.EclipseProjectDependencies;
import com.jfixby.tool.eclipse.dep.EclipseProjectInfo;
import com.jfixby.tool.eclipse.dep.EclipseWorkSpaceSettings;

public class PrintDependencies {
	public static final String WORKSPACE_FOLDER = "D:\\[DEV]\\[CODE]\\[WS-20]";

	public static void main (final String[] args) throws IOException {
		ScarabeiDesktop.deploy();

		final File workspace_folder = LocalFileSystem.newFile(WORKSPACE_FOLDER);
		final EclipseWorkSpaceSettings workspace_settings = EclipseWorkSpaceSettings.readWorkspaceSettings(workspace_folder);

		final Set<EclipseProjectInfo> deps = Collections.newSet();
		final Set<String> ignore = Collections.newSet();
		ignore.add("gdx");
		ignore.add("gdx-jnigen");
		ignore.add("gdx-backend-android");
		ignore.clear();

		{
			final EclipseProjectInfo info = workspace_settings.getProjectInfo("red-parallax");
// info.getDependencies().print();

			deps.add(info);
			collectAllDependencies(info, workspace_settings, deps, ignore);
// projects.print("all");

		}

	}

	private static Set<EclipseProjectInfo> collectAllDependencies (final EclipseProjectInfo info,
		final EclipseWorkSpaceSettings workspace_settings, final Set<EclipseProjectInfo> deps, final Set<String> ignore) {
		final EclipseProjectDependencies diredependencies = info.getDependencies();
		listDeps(info);

		for (final String projectName : diredependencies.getProjectsList()) {
			final EclipseProjectInfo project = workspace_settings.getProjectInfo(projectName);
			collectProjects(deps, project, workspace_settings, ignore);
		}
// list.removeAll(direct);

		return deps;
	}

	private static void listDeps (final EclipseProjectInfo info) {
		final EclipseProjectDependencies deps = info.getDependencies();
		deps.print();
	}

	private static void collectProjects (final Set<EclipseProjectInfo> list, final EclipseProjectInfo project,
		final EclipseWorkSpaceSettings workspace_settings, final Set<String> ignore) {
		listDeps(project);
		if (ignore.contains(project.getProjectName())) {
			return;
		}
		if (ignore(project)) {
			return;
		}
		list.add(project);
		final EclipseProjectDependencies diredependencies = project.getDependencies();
		for (final String projectName : diredependencies.getProjectsList()) {
			final EclipseProjectInfo sub_project = workspace_settings.getProjectInfo(projectName);
			collectProjects(list, sub_project, workspace_settings, ignore);
		}
	}

	private static boolean ignore (final EclipseProjectInfo project) {
		final String projectName = project.getProjectName();
		if (projectName.toLowerCase().startsWith("gdx")) {
// L.d("projectName", projectName);
			return true;
		}

		return false;
	}

}
