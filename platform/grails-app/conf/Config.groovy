import org.apache.log4j.rolling.RollingFileAppender
import org.apache.log4j.rolling.TimeBasedRollingPolicy
import grails.util.Environment

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
//grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

grails.assets.less.compiler = 'less4j'
grails.assets.plugin."twitter-bootstrap".excludes = ["**/*.less"]
grails.assets.plugin."twitter-bootstrap".includes = ["bootstrap.less"]

grails.databinding.dateFormats = [
    'yyyy-MM-dd', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
]


grails.logging.jul.usebridge = false
grails.serverURL = "https://${Environment.current == Environment.PRODUCTION ? 'plattform' : Environment.current.name}.kola-projekt.de"
kola {
    thumbnailSize = 80
    avatarSize = 40
    appDownloadUrl = "https://play.google.com/apps/testing/de.httc.kola${Environment.current == Environment.PRODUCTION ? '' : '.' + Environment.current.name}"
}
de.httc.plugin.user.selfRegistrationEnabled = false
de.httc.plugin.repository.directory = "/srv/kola/${Environment.current.name}/repository"

elasticSearch {
    index.name = "kola-${Environment.current.name}"
    datastoreImpl = "hibernateDatastore"
    includeTransients = false
    client.mode = "node"
    bulkIndexOnStartup = true
}

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL = "http://130.83.139.161:8080/kola-platform"
        elasticSearch.client.mode = "local"
        elasticSearch.index.store.type = "memory"
//        elasticSearch.bulkIndexOnStartup = false

        de.httc.plugin.repository.directory = "./data/repository"
        de.httc.plugin.pushNotification.gcmApiKey = "AIzaSyAJQJjAOE53yqqTbgi3Nj3rfeMjREc-fOo"
    }
    production {
        de.httc.plugin.pushNotification.gcmApiKey = "AIzaSyCEXmn4Ta8wvX8Nb9lIW8GEBGavBknequ8"
    }
    demo {
        de.httc.plugin.pushNotification.gcmApiKey = "AIzaSyAr2gXuBbLcDek4-zGcwvJPA-v9EHpuhOo"
    }
    staging {
        de.httc.plugin.pushNotification.gcmApiKey = "AIzaSyAJQJjAOE53yqqTbgi3Nj3rfeMjREc-fOo"
    }
    lvg {
        de.httc.plugin.pushNotification.gcmApiKey = "AIzaSyDZ71xb2yLt1eaO8qw9pRsPw-Whzlo036s"
    }
}

// log4j configuration
log4j = {
    /*
    log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=C:\\wsimport\\log.txt
log4j.appender.file.MaxFileSize=1MB
log4j.appender.file.MaxBackupIndex=1
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
*/
/*
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '[%d{yyyy-MM-dd HH:mm:ss}] %X{sessionId},%X{user},%m%n')
    }
*/

    def rollingFile = new RollingFileAppender(name:'rollingFileAppender', layout: pattern(conversionPattern:'[%d{yyyy-MM-dd HH:mm:ss}] %X{userAgent},%X{sessionId},%X{user},%X{method},%m%n'))
    // Rolling policy where log filename is logs/app.log.
    // Rollover each day, compress and save in logs/backup directory.
    def rollingPolicy = new TimeBasedRollingPolicy(fileNamePattern: 'logs/backup/app.%d{yyyy-MM-dd}.gz', activeFileName: 'logs/usage.log')
    rollingPolicy.activateOptions()
    rollingFile.setRollingPolicy rollingPolicy

    appenders {
//        appender new org.apache.kafka.log4jappender.KafkaLog4jAppender(name:'kafka', topic:'KOLA_LOG', brokerList:'localhost:9092', compressionType:'none', requiredNumAcks:0, syncSend:true, layout: pattern(conversionPattern:'%c{2} %m%n'))
//        appender new org.springframework.amqp.rabbit.log4j.AmqpAppender(name:'amqp', host:'localhost', port:5672, username:'guest', password:'guest', virtualHost:'/', exchangeName:'', routingKeyPattern:'log-test', layout:pattern(conversionPattern:'[%d{yyyy-MM-dd HH:mm:ss}] %X{userAgent},%X{sessionId},%X{user},%X{method},%m%n'))
        appender rollingFile
        console name: 'stdout'
    }
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    root {
        error 'stdout'
    }

    debug 'grails.app'
    debug rollingFileAppender: 'usagetracking', additivity:false
//    debug amqp:'usagetracking', additivity:false

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.hibernate',
           'org.springframework',
           'net.sf.ehcache.hibernate',
           'org.grails.plugins'

   //debug 'org.codehaus.groovy.grails.web.mapping', 'org.codehaus.groovy.grails.web.mapping.filter'
}

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.userLookup.userDomainClassName = 'de.httc.plugins.user.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'de.httc.plugins.user.UserRole'
grails.plugin.springsecurity.authority.className = 'de.httc.plugins.user.Role'
grails.plugin.springsecurity.securityConfigType = "Annotation"
grails.plugin.springsecurity.apf.storeLastUsername = true
grails.plugin.springsecurity.printStatusMessages = false
grails.plugin.springsecurity.failureHandler.exceptionMappings = [
   'org.springframework.security.authentication.CredentialsExpiredException': '/user/passwordExpired'
]
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                ['permitAll'],
    '/index':           ['permitAll'],
	'/index.gsp':       ['permitAll'],
	'/assets/**':       ['permitAll'],
	'/**/js/**':        ['permitAll'],
	'/**/css/**':       ['permitAll'],
    '/**/images/**':    ['permitAll'],
    '/**/img/**':       ['permitAll'],
    '/**/fonts/**':     ['permitAll'],
	'/**/favicon.ico':  ['permitAll'],
    '/register/**':     ['permitAll'],
    '/user/**':         ['ROLE_ADMIN'],
    '/dbconsole/**':     ['permitAll']
]

// enable basic authentication for api access for app
grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "kola db (proxy)"
grails.plugin.springsecurity.filterChain.chainMap = [
        '/api/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
        '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
]

grails.plugins.twitterbootstrap.fixtaglib = true

grails {
   mail {
        host = "mailserver.kom.e-technik.tu-darmstadt.de"
        port = 465
        props = [
            "mail.smtp.socketFactory.port":"465",
            "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
            "mail.smtp.socketFactory.fallback":"false"]
   }
}
grails.plugin.springsecurity.ui.forgotPassword.emailSubject = "KOLA Benutzerkonto, neues Passwort"
grails.plugin.springsecurity.ui.forgotPassword.emailFrom = "info@kola-projekt.de"
grails.plugin.springsecurity.ui.forgotPassword.emailBody = 'Guten Tag $user.profile.firstName $user.profile.lastName,<br/><br/>bitte klicken Sie&nbsp;<a href="$url">hier</a>, um Ihr KOLA-Passwort neu zu setzen.<br/><br/>Wenn Sie diese Mail nicht angefordert haben, ignorieren Sie sie bitte einfach, es wurden keine Änderungen vorgenommen.<br/><br/>Ihr KOLA-Team'
grails.plugin.springsecurity.ui.register.emailSubject = "KOLA Benutzerkonto"
grails.plugin.springsecurity.ui.register.defaultRoleNames =["ROLE_USER"]
grails.plugin.springsecurity.ui.register.emailFrom = "info@kola-projekt.de"
grails.plugin.springsecurity.ui.register.emailBody = 'Guten Tag $user.profile.firstName $user.profile.lastName,<br/><br/>um das Anlegen des neuen KOLA Benutzerkontos abzuschließen, klicken Sie bitte&nbsp;<a href="$url">hier</a>.<br/><br/>Wenn Sie kein KOLA Benutzerkonto angefordert haben, ignorieren Sie diese Mail bitte einfach.<br/><br/>Ihr KOLA-Team'

//grails.plugin.databasemigration.updateOnStart = true
//grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy']
