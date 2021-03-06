package com.hack23.cia.architecture;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.structurizr.Workspace;
import com.structurizr.analysis.AbstractSpringComponentFinderStrategy;
import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.SpringComponentComponentFinderStrategy;
import com.structurizr.analysis.SpringServiceComponentFinderStrategy;
import com.structurizr.analysis.SupportingTypesStrategy;
import com.structurizr.io.dot.DotWriter;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Location;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.ViewSet;

public class AppPublicSystemDocumentation {

	// Public site https://structurizr.com/share/37264#Enterprise
	// inspired from
	// https://github.com/structurizr/java/blob/master/structurizr-examples/src/com/structurizr/example/SpringPetClinic.java

	private static final long WORKSPACE_ID = 37264;
	private static final String API_KEY = "";
	private static final String API_SECRET = "";

	public static final class SpringRepositoryComponentFinderStrategy extends AbstractSpringComponentFinderStrategy {
		private static final String SPRING_REPOSITORY = "Spring Repository";

		public SpringRepositoryComponentFinderStrategy(SupportingTypesStrategy... strategies) {
			super(strategies);
		}

		@Override
		protected Set<Component> doFindComponents() throws Exception {
			return findInterfacesForImplementationClassesWithAnnotation(org.springframework.stereotype.Repository.class,
					SPRING_REPOSITORY);
		}
	}

	public static void main(String[] args) throws Exception {
		Workspace workspace = new Workspace("Citizen Intelligence Agency", "Public System Documentation");
		Model model = workspace.getModel();
		ViewSet viewSet = workspace.getViews();

		Person userPerson = model.addPerson("User", "User of the system");
		Person adminPerson = model.addPerson("Admin", "Manager of the system");

		SoftwareSystem ciaSystem = model.addSoftwareSystem("Citizen Intelligence Agency System",
				"Tracking politicians like bugs!");

		SoftwareSystem riksdagenApiSystem = model.addSoftwareSystem(Location.External, "data.riksdagen.se",
				"Public API Swedish Parliament data");
		SoftwareSystem worldBankApiSystem = model.addSoftwareSystem(Location.External, "data.wordbank.org",
				"Public API Country indicators");
		SoftwareSystem valApiSystem = model.addSoftwareSystem(Location.External, "www.val.se",
				"Public API Swedish Election data");
		SoftwareSystem esvApiSystem = model.addSoftwareSystem(Location.External, "www.esv.se",
				"Public Data Swedish public sector spending data");

		Container loadBalancerContainer = ciaSystem.addContainer("Loadbalancer", "Loadbalancer",
				"ALB/ELB/Apache/Nginx/HaProxy");

		Container ciaWebContainer = ciaSystem.addContainer("Web Application", "Web Application", "Jetty/Java");
		ComponentFinder componentFinderWeb = new ComponentFinder(ciaWebContainer, "com.hack23.cia",
				new SpringServiceComponentFinderStrategy(), new SpringComponentComponentFinderStrategy(),
				new SpringRepositoryComponentFinderStrategy());
		componentFinderWeb.exclude(".*pagemode.*");
		componentFinderWeb.exclude(".*common.*");
		componentFinderWeb.exclude(".*action.*");
		componentFinderWeb.exclude(".*listener.*");
		componentFinderWeb.exclude(".*ui.*");
		componentFinderWeb.exclude(".*package.*");

		componentFinderWeb.findComponents();

		Container relationalDatabase = ciaSystem.addContainer("Database", "Stores information", "Postgresql");
		relationalDatabase.addTags("Database");

		adminPerson.uses(ciaSystem, "Manages");
		userPerson.uses(ciaSystem, "Uses");
		ciaSystem.uses(riksdagenApiSystem, "Loads data");
		ciaSystem.uses(worldBankApiSystem, "Loads data");
		ciaSystem.uses(valApiSystem, "Loads data");
		ciaSystem.uses(esvApiSystem, "Loads data");
		loadBalancerContainer.uses(ciaWebContainer, "HTTPS/H2");

		ciaWebContainer.uses(relationalDatabase, "JDBC");

		viewSet.createEnterpriseContextView("Enterprise", "Enterprise").addAllElements();
		viewSet.createSystemContextView(ciaSystem, "System context", "System context").addAllElements();
		viewSet.createContainerView(ciaSystem, "Container view", "Application Overview").addAllContainers();
		viewSet.createComponentView(ciaWebContainer, "Web", "Web").addAllComponents();

		Styles styles = viewSet.getConfiguration().getStyles();
		styles.addElementStyle(Tags.COMPONENT).background("#1168bd").color("#ffffff");
		styles.addElementStyle(Tags.CONTAINER).background("#1168bd").color("#ffffff");
		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
		styles.addElementStyle(Tags.PERSON).background("#519823").color("#ffffff").shape(Shape.Person);
		styles.addElementStyle("Database").shape(Shape.Cylinder);

		// StructurizrClient structurizrClient = new StructurizrClient(API_KEY,
		// API_SECRET);
		// structurizrClient.putWorkspace(WORKSPACE_ID, workspace);

		createDotAndPngFiles(workspace);

	}

	private static void createDotAndPngFiles(Workspace workspace) throws IOException {
		StringWriter stringWriter = new StringWriter();
		DotWriter dotWriter = new DotWriter();
		dotWriter.write(workspace, stringWriter);

		String[] split = stringWriter.getBuffer().toString().split("#");

		for (String string : split) {
			if (!string.isEmpty()) {
				String[] split2 = string.split(System.lineSeparator(), 2);
				String fullFilePathDotFile = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator
						+ "target" + File.separator + "site" + File.separator + "architecture" + File.separator
						+ split2[0].trim().replace(" ", "_").replaceAll("_-_", "_") + ".dot";
				FileUtils.writeStringToFile(new File(fullFilePathDotFile), split2[1], Charset.defaultCharset());

				System.out.println("Writing file:" + fullFilePathDotFile);

				List<String> commands = new ArrayList<String>();
				commands.add("/bin/bash");
				commands.add("-c");
				commands.add("dot dot -Tpng -O " + fullFilePathDotFile);

				Runtime r = Runtime.getRuntime();
				try {
					System.out.println("generate png :" + fullFilePathDotFile.replace(".dot", ".png"));
					Process p = r.exec(commands.toArray(new String[commands.size()]));

					p.waitFor();
					BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";

					while ((line = b.readLine()) != null) {
						System.out.println(line);
					}

					b.close();
				} catch (Exception e) {
					System.err.println("Failed to generate png (dot executable missing) for :" + fullFilePathDotFile);
					e.printStackTrace();
				}

			}
		}
	}
}