import org.gradle.api.Project
import java.util.*
import java.io.File
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {
    extra["defaultSnapshotVersion"] = "1.2-SNAPSHOT"

    kotlinBootstrapFrom(BootstrapOption.TeamCity("1.2.0-dev-439", onlySuccessBootstrap = false))

    val repos = listOfNotNull(
            bootstrapKotlinRepo,
            "https://jcenter.bintray.com/",
            "https://plugins.gradle.org/m2",
            "http://repository.jetbrains.com/utils/",
            "http://dl.bintray.com/kotlin/kotlinx",
            "https://repo.gradle.org/gradle/libs-releases-local") // for native-platform

    extra["repos"] = repos

    extra["versions.shadow"] = "2.0.1"

    repositories {
        for (repo in repos) {
            maven(url = repo)
        }
    }

    dependencies {
        classpath("com.gradle.publish:plugin-publish-plugin:0.9.7")
        classpath(kotlinDep("gradle-plugin", bootstrapKotlinVersion))
    }
}

plugins {
    `build-scan`
    idea
}

buildScan {
    setLicenseAgreementUrl("https://gradle.com/terms-of-service")
    setLicenseAgree("yes")
}

val configuredJdks: List<JdkId> =
        getConfiguredJdks().also {
            it.forEach {
                logger.info("Using ${it.majorVersion} home: ${it.homeDir}")
            }
        }

val defaultSnapshotVersion: String by extra
val buildNumber by extra(findProperty("build.number")?.toString() ?: defaultSnapshotVersion)
val kotlinVersion by extra(findProperty("deployVersion")?.toString() ?: buildNumber)

val kotlinLanguageVersion by extra("1.2")

allprojects {
    group = "org.jetbrains.kotlin"
    version = kotlinVersion
}

extra["kotlin_root"] = rootDir

val bootstrapCompileCfg = configurations.create("bootstrapCompile")

repositories {
    for (repo in (rootProject.extra["repos"] as List<String>)) {
        maven(url = repo)
    }
}

dependencies {
    bootstrapCompileCfg(kotlinDep("compiler-embeddable", bootstrapKotlinVersion))
}

val commonBuildDir = File(rootDir, "build")
val distDir = "$rootDir/dist"
val distKotlinHomeDir = "$distDir/kotlinc"
val distLibDir = "$distKotlinHomeDir/lib"
val commonLocalDataDir = "$rootDir/local"
val ideaSandboxDir = "$commonLocalDataDir/ideaSandbox"
val ideaUltimateSandboxDir = "$commonLocalDataDir/ideaUltimateSandbox"
val ideaPluginDir = "$distDir/artifacts/ideaPlugin/Kotlin"
val ideaUltimatePluginDir = "$distDir/artifacts/ideaUltimatePlugin/Kotlin"

extra["distDir"] = distDir
extra["distKotlinHomeDir"] = distKotlinHomeDir
extra["distLibDir"] = project.file(distLibDir)
extra["libsDir"] = project.file(distLibDir)
extra["commonLocalDataDir"] = project.file(commonLocalDataDir)
extra["ideaSandboxDir"] = project.file(ideaSandboxDir)
extra["ideaUltimateSandboxDir"] = project.file(ideaUltimateSandboxDir)
extra["ideaPluginDir"] = project.file(ideaPluginDir)
extra["ideaUltimatePluginDir"] = project.file(ideaUltimatePluginDir)
extra["isSonatypeRelease"] = false

Properties().apply {
    load(File(rootDir, "resources", "kotlinManifest.properties").reader())
    forEach {
        val key = it.key
        if (key != null && key is String)
            extra[key] = it.value
    }
}

extra["JDK_16"] = jdkPath("1.6")
extra["JDK_17"] = jdkPath("1.7")
extra["JDK_18"] = jdkPath("1.8")
extra["JDK_9"] = jdkPathIfFound("9")

extra["versions.intellij"] = "IC-172.4343.14"
extra["versions.protobuf-java"] = "2.6.1"
extra["versions.javax.inject"] = "1"
extra["versions.jsr305"] = "1.3.9"
extra["versions.jansi"] = "1.16"
extra["versions.jline"] = "3.3.1"
extra["versions.junit"] = "4.12"
extra["versions.javaslang"] = "2.0.6"
extra["versions.ant"] = "1.8.2"
extra["versions.android"] = "2.3.1"
extra["versions.kotlinx-coroutines-core"] = "0.14.1"
extra["versions.kotlinx-coroutines-jdk8"] = "0.14.1"
extra["versions.json"] = "20160807"
extra["versions.native-platform"] = "0.14"
extra["versions.ant-launcher"] = "1.8.0"
extra["versions.robolectric"] = "3.1"
extra["versions.org.springframework"] = "4.2.0.RELEASE"
extra["versions.idea.NodeJS"] = "172.3757.32"

val markdownVer =  "4054 - Kotlin 1.0.2-dev-566".replace(" ", "%20") // fixed here, was last with "status:SUCCESS,tag:forKotlin"
extra["markdownParserRepo"] = "https://teamcity.jetbrains.com/guestAuth/repository/download/IntelliJMarkdownParser_Build/$markdownVer/([artifact]_[ext]/)[artifact](.[ext])"

// the former "ideaSdk/core" dir contents without intellij-core.jar
extra["IntellijCoreDependencies"] =
        listOf("annotations.jar",
               "asm-all.jar",
               "guava-*.jar",
               "jdom.jar",
               "jna.jar",
               "log4j.jar",
               "picocontainer.jar",
               "snappy-in-java-*.jar",
               "streamex-*.jar",
               "trove4j.jar",
               "xpp3-1.1.4-min.jar",
               "xstream-*.jar")

extra["nativePlatformVariants"] =
        listOf("windows-amd64",
               "windows-i386",
               "osx-amd64",
               "osx-i386",
               "linux-amd64",
               "linux-i386",
               "freebsd-amd64-libcpp",
               "freebsd-amd64-libstdcpp",
               "freebsd-i386-libcpp",
               "freebsd-i386-libstdcpp")

extra["compilerModules"] = arrayOf(
        ":compiler:util",
        ":compiler:container",
        ":compiler:conditional-preprocessor",
        ":compiler:resolution",
        ":compiler:serialization",
        ":compiler:frontend",
        ":compiler:frontend.java",
        ":compiler:frontend.script",
        ":compiler:cli-common",
        ":compiler:daemon-common",
        ":compiler:daemon",
        ":compiler:ir.tree",
        ":compiler:ir.psi2ir",
        ":compiler:backend-common",
        ":compiler:backend",
        ":compiler:plugin-api",
        ":compiler:light-classes",
        ":compiler:cli",
        ":compiler:incremental-compilation-impl",
        ":js:js.ast",
        ":js:js.serializer",
        ":js:js.parser",
        ":js:js.frontend",
        ":js:js.translator",
        ":js:js.dce",
        ":compiler",
        ":kotlin-build-common",
        ":core:descriptors",
        ":core:descriptors.jvm",
        ":core:deserialization",
        ":core:util.runtime"
)

val coreLibProjects = listOf(
        ":kotlin-stdlib",
        ":kotlin-stdlib-common",
        ":kotlin-stdlib-js",
        ":kotlin-stdlib-jre7",
        ":kotlin-stdlib-jre8",
        ":kotlin-stdlib-jdk7",
        ":kotlin-stdlib-jdk8",
        ":kotlin-test:kotlin-test-common",
        ":kotlin-test:kotlin-test-jvm",
        ":kotlin-test:kotlin-test-junit",
        ":kotlin-test:kotlin-test-js",
        ":kotlin-reflect"
)

val gradlePluginProjects = listOf(
        ":kotlin-gradle-plugin",
        ":kotlin-gradle-plugin:plugin-marker",
        ":kotlin-gradle-plugin-api",
//        ":kotlin-gradle-plugin-integration-tests",  // TODO: build fails
        ":kotlin-allopen",
        ":kotlin-allopen:plugin-marker",
        ":kotlin-annotation-processing-gradle",
        ":kotlin-noarg",
        ":kotlin-noarg:plugin-marker",
        ":kotlin-sam-with-receiver"
)

apply {
    from("libraries/commonConfiguration.gradle")
    from("libraries/configureGradleTools.gradle")
}

apply {
    if (extra["isSonatypeRelease"] as? Boolean == true) {
        logger.info("Applying configuration for sonatype release")
        from("libraries/prepareSonatypeStaging.gradle")
    }
}

val importedAntTasksPrefix = "imported-ant-update-"

// TODO: check the reasons of import conflict with xerces
//ant.importBuild("$rootDir/update_dependencies.xml") { antTaskName -> importedAntTasksPrefix + antTaskName }

tasks.matching { task ->
    task.name.startsWith(importedAntTasksPrefix)
}.forEach {
    it.group = "Imported ant"
}

//task("update-dependencies") {
//    dependsOn(tasks.getByName(importedAntTasksPrefix + "update"))
//}

fun Project.allprojectsRecursive(body: Project.() -> Unit) {
    this.body()
    this.subprojects { allprojectsRecursive(body) }
}

fun Task.listConfigurationContents(configName: String) {
    doFirst {
        println("$configName configuration files:\n${project.configurations[configName].allArtifacts.files.files.joinToString("\n  ", "  ")}")
    }
}

val defaultJvmTarget = "1.8"
val defaultJavaHome = jdkPath(defaultJvmTarget!!)
val ignoreTestFailures by extra(project.findProperty("ignoreTestFailures")?.toString()?.toBoolean() ?: project.hasProperty("teamcity"))

allprojects {

    jvmTarget = defaultJvmTarget
    javaHome = defaultJavaHome

    // There are problems with common build dir:
    //  - some tests (in particular js and binary-compatibility-validator depend on the fixed (default) location
    //  - idea seems unable to exclude common builddir from indexing
    // therefore it is disabled by default
//    buildDir = File(commonBuildDir, project.name)

    repositories {
        for (repo in (rootProject.extra["repos"] as List<String>)) {
            maven { setUrl(repo) }
        }
        ivy {
            artifactPattern(rootProject.extra["markdownParserRepo"] as String)
        }
    }
    configureJvmProject(javaHome!!, jvmTarget!!)

    val commonCompilerArgs = listOf("-Xallow-kotlin-package")
    
    tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
        kotlinOptions {
            languageVersion = kotlinLanguageVersion
            apiVersion = kotlinLanguageVersion
            freeCompilerArgs = commonCompilerArgs
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
        kotlinOptions {
            freeCompilerArgs = commonCompilerArgs + listOf("-Xnormalize-constructor-calls=enable")
        }
    }

    tasks.withType(VerificationTask::class.java as Class<Task>) {
        (this as VerificationTask).ignoreFailures = ignoreTestFailures
    }

    tasks.withType<Javadoc> {
        enabled = false
    }

    task<Jar>("javadocJar") {
        classifier = "javadoc"
    }

    tasks.withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    task("listArchives") { listConfigurationContents("archives") }

    task("listRuntimeJar") { listConfigurationContents("runtimeJar") }

    task("listDistJar") { listConfigurationContents("distJar") }

    afterEvaluate {
        logger.info("configuring project $name to compile to the target jvm version $jvmTarget using jdk: $javaHome")
        if (javaHome != defaultJavaHome || jvmTarget != defaultJvmTarget) {
            configureJvmProject(javaHome!!, jvmTarget!!)
        }
    }
}

task<Copy>("dist") {
    into(distDir)
    from(files("compiler/cli/bin")) { into("kotlinc/bin") }
    from(files("license")) { into("kotlinc/license") }
}

val compilerCopyTask = task<Copy>("idea-plugin-copy-compiler") {
    dependsOnTaskIfExistsRec("dist")
    into(ideaPluginDir)
    from(distDir) { include("kotlinc/**") }
}

task<Copy>("ideaPlugin") {
    dependsOn(compilerCopyTask)
    dependsOnTaskIfExistsRec("idea-plugin")
    shouldRunAfter(":prepare:idea-plugin:idea-plugin")
    into("$ideaPluginDir/lib")
}

tasks {
    "clean" {
        doLast {
            delete("$buildDir/repo")
            delete(distDir)
        }
    }

    // TODO: copied from TeamCityBuild.xml (with ultimate-related modification), consider removing after migrating from it
    "cleanupArtifacts" {
        doLast {
            delete(ideaPluginDir)
            delete(ideaUltimatePluginDir)
        }
    }

    "coreLibsTest" {
        (coreLibProjects + listOf(
                ":kotlin-stdlib:samples",
                ":kotlin-test:kotlin-test-js:kotlin-test-js-it",
                ":tools:binary-compatibility-validator"
        )).forEach {
            dependsOn(it + ":check")
        }
    }

    "gradlePluginTest" {
        gradlePluginProjects.forEach {
            dependsOn(it + ":check")
        }
    }

    "gradlePluginIntegrationTest" {
        dependsOn(":kotlin-gradle-plugin-integration-tests:check")
    }

    "jvmCompilerTest" {
        dependsOn("dist")
        dependsOn(":compiler:test",
                  ":compiler:container:test",
                  ":compiler:tests-java8:test")
    }

    "jsCompilerTest" {
        dependsOn(":js:js.tests:test")
    }

    "scriptingTest" {
        dependsOn("dist")
        dependsOn(":kotlin-script-util:test")
    }

    "compilerTest" {
        dependsOn("jvmCompilerTest")
        dependsOn("jsCompilerTest")

        dependsOn("scriptingTest")
        dependsOn(":kotlin-build-common:test")
    }

    "examplesTest" {
        dependsOn("dist")
        (project(":examples").subprojects + project(":kotlin-gradle-subplugin-example")).forEach { p ->
            dependsOn("${p.path}:check")
        }
    }

    "distTest" {
        dependsOn("compilerTest")
        dependsOn("gradlePluginTest")
        dependsOn("examplesTest")
    }

    "androidCodegenTest" {
        dependsOn(":compiler:android-tests:test")
    }

    "jps-tests" {
        dependsOn("dist")
        dependsOn(":jps-plugin:test")
    }

    "idea-plugin-main-tests" {
        dependsOn("dist")
        dependsOn(":idea:test")
    }

    "idea-plugin-additional-tests" {
        dependsOn("dist")
        dependsOn(":idea:idea-gradle:test",
                  ":idea:idea-maven:test",
                  ":j2k:test",
                  ":eval4j:test")
    }

    "idea-plugin-tests" {
        dependsOn("dist")
        dependsOn("idea-plugin-main-tests",
                  "idea-plugin-additional-tests")
    }

    "android-ide-tests" {
        dependsOn("dist")
        dependsOn(":plugins:android-extensions-ide:test",
                  ":idea:idea-android:test",
                  ":kotlin-annotation-processing:test")
    }

    "plugins-tests" {
        dependsOn("dist")
        dependsOn(":plugins:plugins-tests:test",
                  ":kotlin-annotation-processing:test",
                  ":kotlin-source-sections-compiler-plugin:test",
                  ":plugins:uast-kotlin:test",
                  ":kotlin-annotation-processing-gradle:test")
    }


    "ideaPluginTest" {
        dependsOn(
                "idea-plugin-tests",
                "jps-tests",
                "plugins-tests",
                "android-ide-tests",
                ":generators:test"
        )
    }


    "test" {
        doLast {
            throw GradleException("Don't use directly, use aggregate tasks *-check instead")
        }
    }
    "check" { dependsOn("test") }
}

// TODO: copied from build.xml (used on TC), reconsider location and dependencies after complete migration
val `zip-compiler` by task<Zip> {
    destinationDir = file(distDir)
    archiveName = "kotlin-compiler-$buildNumber.zip"
    from(distKotlinHomeDir) {
        exclude("bin/**")
        into("kotlinc")
    }
    from("$distKotlinHomeDir/bin") {
        include("*.bat")
        into("kotlinc/bin")
        fileMode = 0b110100100
    }
    from("$distKotlinHomeDir/bin") {
        exclude("*.bat")
        into("kotlinc/bin")
        fileMode = 0b111101101
    }
}

// TODO: copied from build.xml (used on TC), reconsider location and dependencies after complete migration
val `zip-test-data` by task<Zip> {
    destinationDir = file(distDir)
    archiveName = "kotlin-test-data.zip"
    from("compiler/testData") { into("compiler") }
    from("idea/testData") { into("ide") }
    from("idea/idea-completion/testData") { into("ide/completion") }
}

// TODO: copied from TeamCityBuild.xml (excluding some parts), consider implementing patching in the appropriate projects

val pluginXmlDir = "$rootDir/idea/src/META-INF"
val pluginXmlPath = "$pluginXmlDir/plugin.xml"
val pluginXmlBackupDir = "$buildDir/plugin_xml_backup"
val pluginXmlBackupPath = "$pluginXmlBackupDir/plugin.xml"

val backupTemplateFile by task<Copy> {
    from(pluginXmlPath)
    into(pluginXmlBackupDir)
}

val writePluginVersionToTemplateFile by task<Copy> {
    dependsOn(backupTemplateFile)
    from(pluginXmlBackupPath)
    into(pluginXmlDir)
    filter { it.replace("@snapshot@", "$buildNumber") }
}

val restoreTemplateFile by task<Copy> {
    from(pluginXmlBackupPath)
    into(pluginXmlDir)
}

val revertTemplateFiles by task<Delete> {
    dependsOn(restoreTemplateFile)
    delete(pluginXmlBackupDir)
}

// TODO: copied from build.xml (used on TC), reconsider location, logic and dependencies after complete migration
val zipArtifacts by task<Zip> {
    val dest = File(System.getProperty("plugin.zip") ?: "$distDir/artifacts/kotlin-plugin-$buildNumber.zip")
    val src = System.getProperty("pluginArtifactDir") ?: ideaPluginDir
    destinationDir = dest.parentFile
    archiveName = dest.name
    from(src) {
        exclude("kotlinc/bin/**")
        into("Kotlin")
    }
    from("$src/kotlinc/bin") {
        include("*.bat")
        into("Kotlin/kotlinc/bin")
        fileMode = 0b110100100
    }
    from("$src/kotlinc/bin") {
        exclude("*.bat")
        into("Kotlin/kotlinc/bin")
        fileMode = 0b111101101
    }
}

configure<IdeaModel> {
    module {
        excludeDirs = files(
                project.buildDir,
                commonLocalDataDir,
                ".gradle",
                "dependencies",
                "dist"
        ).toSet()
    }
}

fun jdkPathIfFound(version: String): String? {
    val jdkName = "JDK_${version.replace(".", "")}"
    val jdkMajorVersion = JdkMajorVersion.valueOf(jdkName)
    return configuredJdks.find { it.majorVersion == jdkMajorVersion }?.homeDir?.canonicalPath
}

fun jdkPath(version: String): String = jdkPathIfFound(version)
        ?: throw GradleException ("Please set environment variable JDK_${version.replace(".", "")} to point to JDK $version installation")

fun Project.configureJvmProject(javaHome: String, javaVersion: String) {
    tasks.withType<JavaCompile> {
        options.isFork = true
        options.forkOptions.javaHome = file(javaHome)
        options.compilerArgs.add("-proc:none")
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jdkHome = javaHome
        kotlinOptions.jvmTarget = javaVersion
    }

    tasks.withType<Test> {
        executable = File(javaHome, "bin/java").canonicalPath
    }
}
