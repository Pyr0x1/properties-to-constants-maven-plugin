# properties-to-constants-maven-plugin
A Maven plugin that generates a class containing all property keys from a property file as constants.

## Getting Started

The project is a single Mojo linked to generate-sources as default phase.

### Prerequisites

In order to use the plugin you'll need to clone this project and install it in your Maven repository:

```sh
mvn install
```

### Instructions

To use the plugin you'll need to add it in your project's pom:

```xml
<plugin>
	<groupId>it.pyrox</groupId>
	<artifactId>properties-to-constants-maven-plugin</artifactId>
	<version>0.0.1</version>
	<executions>
		<execution>
			<goals>
				<goal>convert</goal>
			</goals>
			<configuration>
				<sourcePropertyPath>locale/strings.properties</sourcePropertyPath>
				<destPackageName>it.pyrox.util</destPackageName>
				<destClassName>Constants</destClassName>
			</configuration>
		</execution>
	</executions>
</plugin>
```

where 

* **sourcePropertyPath** is the path of the property file you want to read
* **destPackageName** is the destination package of the generated class
* **destClassName** is the generated class name

by default the class will be generated under *target/generated-sources*.

In order to automatically mark the directory containing the generated class as a source directory in the classpath you can use the [build-helper-maven-plugin](https://www.mojohaus.org/build-helper-maven-plugin/) with the **add-source** goal.