package it.pyrox.maven.plugin.properties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * This plugin takes a property file and generates a class containing all keys as constants.
 */

@Mojo(name = "convert", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PropertiesToConstantsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
	private List<Resource> resources;

	@Parameter(defaultValue="${project.build.directory}", required = true, readonly = true)
	private String targetFolder;

    /**
     * Folder contained in src/main/resources containing the property file we want to consider.
     */
	@Parameter(property = "sourceResourcesSubDir", required = false)
	String sourceResourcesSubDir;

	/**
     * Name of the property file to consider.
     */
	@Parameter(property = "sourcePropertyName", required = true)
	String sourcePropertyName;

	/**
     * Package of the generated class.
     */
	@Parameter(property = "destPackageName", required = true)
	String destPackageName;

	/**
     * Name of the generated class.
     */
	@Parameter(property = "destClassName", required = true)
	String destClassName;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Log log = getLog();
		// Optional parameter, if not specified use empty string so path concatenation keeps working
		if (sourceResourcesSubDir == null) {
			sourceResourcesSubDir = "";
		}
		if (resources != null && !resources.isEmpty()) {
			ConstantClassGenerator generator = createAndPopulateGenerator();
			if (!generator.isBodyEmpty()) {
				writeGeneratedClass(generator);
			}
			else {
				log.info("Generated class would be empty, skipping generation");
			}
		}
		else {
			log.error("No resources found");
		}
	}

	private ConstantClassGenerator createAndPopulateGenerator() {
		Log log = getLog();
		String resourceDirectory = resources.get(0).getDirectory();
		log.info("Found resource folder at \"" + resourceDirectory + "\"");
		Path completeSourcePath = Paths.get(resourceDirectory, sourceResourcesSubDir, sourcePropertyName);
		ConstantClassGenerator generator = new ConstantClassGenerator(destPackageName, destClassName);

		try (Scanner scanner = new Scanner(completeSourcePath)) {
			log.info("Found resource file at \"" + completeSourcePath + "\"");
			log.info("Start reading properties file...");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				generator.addConstant(line);
			}
			log.info("Completed reading. Read " + generator.getNumberOfProperties() + " properties");
		} catch (Exception e) {
			log.error("Error while reading file at path \"" + completeSourcePath.toString() + "\"");
		}

		return generator;
	}

	private void writeGeneratedClass(ConstantClassGenerator generator) {
		Log log = getLog();
		Path destinationPath = Paths.get(targetFolder, "generated-sources", convertPackageToDirTree(destPackageName));
		try {
			Files.createDirectories(destinationPath);
		} catch (Exception e) {
			log.error("Error while generating directories with path \"" + destinationPath.toString() + "\"");
		}
		Path filePath = Paths.get(destinationPath.toString(), destClassName + ".java");
		log.info("Start writing constants class...");
		try {
			Files.write(filePath, generator.generate().getBytes());
			log.info("Written generated class at \"" + filePath.toString() + "\"");
		} catch (Exception e) {
			log.error("Error while generating file with path \"" + filePath.toString() + "\"");
		}
	}

	private String convertPackageToDirTree(String inputPackage) {
		return inputPackage.replace(".", File.separator);
	}
}
