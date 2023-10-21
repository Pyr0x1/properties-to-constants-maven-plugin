package it.pyrox.maven.plugin.properties;

public class ConstantClassGenerator {

	private String prologue;
	private String body;
	private String epilogue;
	private Integer numberOfProperties;

	public ConstantClassGenerator(String classPackage, String className) {
		StringBuilder builder = new StringBuilder();
		builder.append("package ");
		builder.append(classPackage);
		builder.append(";");
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append("public final class ");
		builder.append(className);
		builder.append(" {");
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append("\t");
		builder.append("private ");
		builder.append(className);
		builder.append("() {} // Prevents undesired inheritance");
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		this.prologue = builder.toString();
		this.body = "";
		this.epilogue = "}";
		this.numberOfProperties = 0;
	}

	public String generate() {
		return this.prologue + this.body + this.epilogue;
	}

	public void addConstant(String line) {
		boolean isPropertyLine = false;

		if (line != null && !line.isEmpty() && !line.trim().startsWith("#")) {
			String[] splitted = line.split("=");
			if (splitted.length == 2) {
				isPropertyLine = true;
				String key = splitted[0];
				String constantName = key.toUpperCase().replace("-", "_").replace(".", "_");
				StringBuilder builder = new StringBuilder(body);
				builder.append("\t");
				builder.append("public static final String ");
				builder.append(constantName);
				builder.append(" = \"");
				builder.append(key);
				builder.append("\";");
				builder.append(System.lineSeparator());
				this.body = builder.toString();
			}
		}

		if (isPropertyLine) {
			this.numberOfProperties++;
		}
	}

	public boolean isBodyEmpty() {
		return this.body.isEmpty();
	}

	public Integer getNumberOfProperties() {
		return numberOfProperties;
	}
}
