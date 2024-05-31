import org.xml.sax.SAXParseException
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayOutputStream
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import javax.xml.validation.SchemaFactory
import javax.xml.parsers.DocumentBuilderFactory

val junitJupiterVersion: String by properties
val jandexVersion: String by properties
val cdiApiVersion: String by properties
val facesApiVersion: String by properties
val servletApiVersion: String by properties
val hibernateCoreVersion: String by properties
val postgreSQLVersion: String by properties
val facesVersion: String by properties
val primefacesVersion: String by properties
val injectApiVerson: String by properties
val guavaVersion: String by properties

val webAppPath: String by properties
val webXmlPath: String by properties
val classesMainPath: String by properties
val classesTestPath: String by properties
val mainResourcesPath: String by properties
val mainWebappPath: String by properties

val jarName: String by properties
val jarVersion: String by properties

val trunkPath: String by properties

plugins {
    java
}

group = "model"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")

    implementation("io.smallrye:jandex:${jandexVersion}")
    implementation("jakarta.enterprise:jakarta.enterprise.cdi-api:${cdiApiVersion}")
    implementation("jakarta.faces:jakarta.faces-api:${facesApiVersion}")
    implementation("jakarta.servlet:jakarta.servlet-api:${servletApiVersion}")
    implementation("org.hibernate.orm:hibernate-core:${hibernateCoreVersion}")
    implementation("org.postgresql:postgresql:${postgreSQLVersion}")
    implementation("org.glassfish:jakarta.faces:${facesVersion}")
    implementation("org.primefaces:primefaces:${primefacesVersion}")
    implementation("jakarta.inject:jakarta.inject-api:${injectApiVerson}")
    implementation("com.google.guava:guava:${guavaVersion}")
}

tasks.register<Test>("lab-test") {
    dependsOn("lab-build")
    //testClassesDirs = sourceSets["test"].output.classesDirs
    //classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
}

tasks.register<JavaCompile>("lab-compile") {
    source = sourceSets["main"].java
    classpath = sourceSets["main"].compileClasspath
    destinationDirectory.set(file(classesMainPath))
    source = sourceSets["test"].java
    classpath = sourceSets["test"].compileClasspath
    destinationDirectory.set(file(classesTestPath))
}

tasks.register<Jar>("lab-build") {
    dependsOn("lab-compile")
    from(classesMainPath)
    from(mainResourcesPath)
    from(mainWebappPath)
    archiveBaseName.set(jarName)
    archiveVersion.set(jarVersion)
    manifest {
        attributes["Main-Class"] = "model.Result"
    }

    doLast {
        println("Project have been build")
    }
}

tasks.register("lab-clean") {
    doLast {
        delete("build")
    }
}

tasks.register("lab-xml") {
    doLast {
        val saxFactory = SAXParserFactory.newInstance()
        saxFactory.isNamespaceAware = true

        val dtdFactory = DocumentBuilderFactory.newInstance()
        dtdFactory.isNamespaceAware = true
        dtdFactory.isValidating = true

        fileTree(mapOf("dir" to ".", "include" to "**/*.xml", "exclude" to listOf("**/.idea/**", "**/build/**", "**/out/**"))).forEach { file ->
            try {
                val xmlContent = file.readText()
                if (xmlContent.contains("DOCTYPE")) {
                    // Валидация DTD
                    val builder = dtdFactory.newDocumentBuilder()
                    builder.setErrorHandler(object : DefaultHandler() {
                        override fun error(e: SAXParseException) {
                            println("Error: ${e.message}")
                        }

                        override fun fatalError(e: SAXParseException) {
                            println("Fatal error: ${e.message}")
                        }

                        override fun warning(e: SAXParseException) {
                            println("Warning: ${e.message}")
                        }
                    })
                    builder.parse(file)
                } else if (xmlContent.contains("xsi:schemaLocation")) {
                    // Валидация XSD
                    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    val schema = schemaFactory.newSchema()
                    saxFactory.schema = schema
                    val parser = saxFactory.newSAXParser()
                    val reader = parser.xmlReader
                    reader.errorHandler = object : DefaultHandler() {
                        override fun error(e: SAXParseException) {
                            println("Error: ${e.message}")
                        }

                        override fun fatalError(e: SAXParseException) {
                            println("Fatal error: ${e.message}")
                        }

                        override fun warning(e: SAXParseException) {
                            println("Warning: ${e.message}")
                        }
                    }
                    reader.parse(file.absolutePath)
                }
                println("Validated successfully: $file")
            } catch (e: Exception) {
                println("Validation failed: $file, due to ${e.message}")
            }
        }
    }
}

tasks.register("lab-diff") {
    doLast {
        val mutableClasses = File("mutable_classes.txt").readLines()
        println("mutableClasses: $mutableClasses")
        val byteOut = ByteArrayOutputStream()
        project.exec {
	    workingDir = file(trunkPath)
            commandLine = "svn status".split(" ")
            standardOutput = byteOut
        }
        val output = byteOut.toByteArray().toString(Charsets.UTF_8)
        val changedFiles = output.lines().filter { it.startsWith("M ") || it.startsWith("! ") }
                .map { it.trim().split("\\s+".toRegex(), 2)[1] }
		.filter {! it.contains(".gradle") }
        println("changedFiles: $changedFiles")
	
	val filesToCommit = changedFiles.filter { changedFile ->
	    mutableClasses.any { mutableFile -> changedFile.contains(mutableFile) }
	}

	println("filesToCommit: $filesToCommit ")

        if (filesToCommit.isNotEmpty()) {
            println("Changes found in specified classes.")

            println("Current directory: ${file(trunkPath).absolutePath}")
	    val commitCommand = listOf ("svn", "commit", "-m", "'Automated commit'") + filesToCommit

            project.exec {
                workingDir = file(trunkPath)
                commandLine = commitCommand
            }
        } else {
            println("No changes in specified classes.")
        }
    }
}
