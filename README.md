KOLA
====

| Releases ||
| -------- | ----------- |
| Server   | path to data files to supply the data that will be passed into templates. |
| App      | https://play.google.com/apps/de.httc.kola |

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
