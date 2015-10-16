KOLA
====

Building the server platform
------------

**Requirements**

- Java 8
- Grails < 3.0

```bash
$ cd platform
$ grails war
```

To run the server platform in dev mode, use

```bash
$ grails run-app
```

and point your browser at http://localhost:8080/platform


Building the App
------------

**Requirements**

- Java 8 (for targeting Android)
- ?? (for targeting IOS)
- Node

```bash
$ cd app/kola
$ npm install
$ ionic build [android | ios]
```

To run the app in dev mode, use

```bash
$ ionic serve --lab
```
