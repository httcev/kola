grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.war.file = "target/${appName}-${grails.util.Environment.current.name}-${appVersion}.war"

grails.project.fork = [
	// configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
	//  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

	// configure settings for the test-app JVM, uses the daemon by default
	test: [maxMemory: 4096, minMemory: 64, debug: false, maxPerm: 512, daemon:true],
	// configure settings for the run-app JVM
	run: [maxMemory: 4096, minMemory: 64, debug: false, maxPerm: 512, forkReserve:false],
	// configure settings for the run-war JVM
	war: [maxMemory: 4096, minMemory: 64, debug: false, maxPerm: 512, forkReserve:false],
	// configure settings for the Console UI JVM
	console: [maxMemory: 4096, minMemory: 64, debug: false, maxPerm: 512]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
	pom true
	// inherit Grails' default dependencies
	inherits("global") {
		// specify dependency exclusions here; for example, uncomment this to disable ehcache:
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

	repositories {
		inherits true // Whether to inherit repository definitions from plugins

		grailsPlugins()
		grailsHome()
		mavenLocal()
		grailsCentral()
		mavenCentral()
		// uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
		//mavenRepo "http://repository.codehaus.org"
		//mavenRepo "http://download.java.net/maven/2/"
		//mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://repo.grails.org/grails/core"

		// needed for apache imaging (used to remove exif metadata from uploaded images)
		mavenRepo "https://repository.apache.org/content/repositories/snapshots/"
	}

	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
		// runtime 'mysql:mysql-connector-java:5.1.29'
		// runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
		test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
	}

	plugins {
		// plugins for the build system only
/*
		build ":tomcat:7.0.55.2" // or ":tomcat:8.0.20"

		compile ":scaffolding:2.1.2"
		compile ":cache:1.1.8"
		compile ":asset-pipeline:2.1.5"
		//compile ":spring-security-core:2.0-RC5"
		provided ":less-asset-pipeline:2.3.0"

		// plugins needed at runtime but not for compilation
		runtime ":hibernate4:4.3.8.1" // or ":hibernate:3.6.10.18"
		runtime ":database-migration:1.4.1"
		runtime ":jquery:1.11.1"
		runtime ":twitter-bootstrap:3.3.4"
		runtime ":font-awesome-resources:4.3.0.1"

		runtime (":elasticsearch:0.0.4.6") {
			excludes "groovy-all" // elastic search pulls in old groovy version 2.4.3
		}
		runtime ":quartz:1.0.2"

		compile ":webflow:2.1.0"
		compile ":rendering:1.0.0"
		//compile (":spring-security-ui:1.0-RC2") { excludes "jquery-ui", "famfamfam" }
		compile ":mail:1.0.7"
*/
	}
}
/*
grails.plugin.location."httc-common" = "../../grails-plugins/common"
grails.plugin.location."httc-lrs" = "../../grails-plugins/lrs"
grails.plugin.location."httc-user" = "../../grails-plugins/user"
grails.plugin.location."httc-push-notification" = "../../grails-plugins/push-notification"
grails.plugin.location."httc-repository" = "../../grails-plugins/repository"
grails.plugin.location."httc-qaa" = "../../grails-plugins/qaa"
grails.plugin.location."httc-taxonomy" = "../../grails-plugins/taxonomy"
*/
