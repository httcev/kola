KOLA
====

Project page: http://www.kola-projekt.de

| Releases ||
| -------- | ----------- |
| Server   | http://repo.httc.de/artifactory/libs-release/de/httc/kola-platform/2.0.10/kola-platform-2.0.10.war |
| App      | https://play.google.com/store/apps/details?id=de.httc.kola |

Building the server platform
------------

**Requirements**

- Java 8
- Apache Maven 3 (https://maven.apache.org/)

```bash
$ cd platform
$ mvn package
```

The web application's `war` file will then be located in the `target` directory, ready to be deployed to an application server, e.g. Apache Tomcat.

Server configuration
------------
Create a file named `kola-platform-config.properties` in your application server's directory. You can configure/override these settings:

```properties
grails.serverURL = <FILL-IN-YOUR-BASE-URL-HERE>
de.httc.plugin.repository.directory = ./repo
elasticSearch.index.name = kola-production
dataSource.url = jdbc:h2:./data/db/kola;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE;TRACE_LEVEL_FILE=0
```

Building the App
------------

**Requirements**

- Java 8 (for targeting Android)
- ?? (for targeting IOS)
- Node.js (https://nodejs.org/en/download/package-manager/)
- Bower (`npm install -g bower`)
- Ionic (`npm install -g ionic`)

```bash
$ cd app/kola
$ npm install
$ bower install
$ ionic build [android | ios]
```

To run the app in dev mode:

```bash
$ ionic serve --lab
```
