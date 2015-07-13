package kola

import java.io.InputStream;
import java.io.InputStreamReader;

import groovy.json.JsonSlurper
import org.lightcouch.CouchDbClient


class CouchSynchronizerJob {
    static triggers = {
		//simple name: 'mySimpleTrigger', startDelay: 1, repeatInterval: 10000
		//cron name: 'myTrigger', cronExpression: "0 45 10 1/1 * ? *"
    }
    def group = "CronJobs"
    def description = "Synchronize local db with couch db"
    def concurrent = false

    def couchdbUrl
    def couchdbAdminUser
    def couchdbAdminPass
    def couchSynchronizerService

    def execute() {
		try {
			println hashCode()
			def settings = CouchSynchronizerSettings.findOrSaveById(1)
			println "RUN -> lastSync=$settings.lastSync, url=$couchdbUrl, admin=$couchdbAdminUser"
			def now = new Date()
			def since = settings.lastSync
			if (!since) {
				since = new Date(0)
			}
			checkUsers(since, now)
			def changedAssets = Asset.findAllByLastUpdatedBetween(since, now)
			if (changedAssets) {
				changedAssets.each {
					println "---> changed asset: " + it
				}

				settings.lastSync = now
				settings.save()
			}


		    log.info "CouchSynchronizerJob run!"
		}
		catch(Exception e) {
		    e.printStackTrace()
		}
    }

    def checkUsers(since, now) {
		def changedUsers = User.findAllByLastUpdatedBetween(since, now)
		changedUsers?.each {
			couchSynchronizerService.updateUsers(it)
		}
    }
/*
    def _fetchTaxonomies(baseUrl) {
	log.info "Deleting existing taxonomies..."
	TaxonomyTerm.getAll()*.delete()
	Taxonomy.getAll()*.delete()
	//TaxonomyTerm.executeUpdate('delete from TaxonomyTerm')
	sessionFactory.currentSession.flush()
	//TaxonomyTerm.where { }.deleteAll()
	//Taxonomy.where { }.deleteAll()

	def url = baseUrl + "/taxonomies"
	def is
	def taxonomies
	try {
	    log.info "Fetching taxonomies list from ${url}..."
	    is = _authorizeConnection(url).getInputStream()
	    //is = new java.io.FileInputStream("/home/tittel/taxonomies.json")
	    taxonomies = new JsonSlurper().parse(is)
	}
	finally {
	    is?.close()
	}
	taxonomies?.each {
	    try {
		log.info "Fetching taxonomy from ${url + '/' + it}..."
		is = _authorizeConnection(url + "/${it}").getInputStream()
		//is = new java.io.FileInputStream("/home/tittel/${it}.json")
		def json = new JsonSlurper().parse(is)
		if (json.primary) { // || json.secondary) {
		    def type = json.primary ? Taxonomy.Type.PRIMARY : (json.secondary ? Taxonomy.Type.SECONDARY : Taxonomy.Type.DEFAULT)
		    println "-------------------- parsing TAX ${json.label}: type=${type}"
		    println "-------------------- existing TAXS ${Taxonomy.list()}"
		    def tax = new Taxonomy(id:json.id, label:json.label, type:type)
		    tax.save()
		    json.terms?.each {
			tax.addToTerms(_parseTermRecursive(it, tax.type == Taxonomy.Type.PRIMARY, []))
		    }
		    println "--- tax save"
		    tax.save(true)

		    if (tax.hasErrors()) {
			println tax.errors
			throw new RuntimeException()
		    }
		}
	    }
	    finally {
		is?.close()
	    }
	}
    }

    def _parseTermRecursive(json, primary, ids) {
	//println "parsing term ${term.label} "
	if (ids.contains(json.id)) {
	    throw new RuntimeException("duplicate id: ${json.id}")
	}
	ids.add(json.id)
	def term = new TaxonomyTerm(id:json.id, label:json.label, isPrimaryDomain:primary)
	println "${term} -> ${term.isPrimaryDomain}"
	term.save()
	//sessionFactory.currentSession.evict(term)

	json.children?.each {
	    def child = _parseTermRecursive(it, primary, ids)
	    term.addToChildren(child)
	}
	return term
    }

    def _fetchInteractions(baseUrl) {
	println "---------------- Current interaction count=" + Interaction.count()
	def lastUpdateDate;
	def newestInteracion = Interaction.list([max:1,sort:"dateCreated",order:"desc"])
	if (newestInteracion) {
	    lastUpdateDate = newestInteracion.first().dateCreated.format('yyyy-MM-dd')
	}
	def url = baseUrl + "/interactions"
	def user = grailsApplication.config.helpcenter.remoteUser
	def pass = grailsApplication.config.helpcenter.remotePass
	if (lastUpdateDate) {
	    println "--- fetching interactions since $lastUpdateDate"
	    url += "?since=" + lastUpdateDate
	}

	def is = null
	try {
	    log.info "Fetching interactions from ${url}..."
	    is = _authorizeConnection(url).getInputStream();
	    //is = new java.io.FileInputStream("/home/tittel/interactions.txt")
	    return new JsonSlurper().parse(is)
	}
	finally {
	    is?.close()
	}
    }

    def _parseInteractions(json) {
	json?.each {
	    def userId = it.userid
	    def verb = Interaction.Verb.valueOf(it.verb.toUpperCase())
	    def subjectType = it.subjecttype ? Interaction.Type.valueOf(it.subjecttype.toUpperCase()) : null
	    def objectType = it.objecttype ? Interaction.Type.valueOf(it.objecttype.toUpperCase()) : null
	    def subjectId = it.subjectid ?: null
	    def objectId = it.objectid ?: null
	    def subjectContent = it.subjectcontent ?: null

	    def interaction = Interaction.findWhere(userId:userId, verb:verb, subjectType:subjectType, objectType:objectType, subjectId:subjectId, objectId:objectId)//, subjectContent:subjectContent)
	    if (!interaction) {
		interaction = new Interaction(userId:userId, verb:verb, subjectType:subjectType, objectType:objectType, subjectId:subjectId, objectId:objectId, subjectContent:subjectContent).save()
		if (interaction.hasErrors()) {
		    println interaction.errors
		}
	    }
	    sessionFactory.currentSession.evict(interaction)
	}
    }

    def _authorizeConnection(url) {
		def user = grailsApplication.config.helpcenter.remoteUser
		def pass = grailsApplication.config.helpcenter.remotePass

		URL endpoint = new URL(url);
		HttpURLConnection con = (HttpURLConnection) endpoint.openConnection();
		con.setRequestProperty("Authorization", "Basic " + ((user + ":" + pass).bytes.encodeBase64().toString()))
		return con
    }
*/    
}
