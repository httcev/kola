package kola

import java.io.InputStream;
import java.io.InputStreamReader;

import groovy.json.JsonSlurper
import org.lightcouch.CouchDbClient


class CouchSynchronizerJob {
    static triggers = {
		//simple name: 'syncTrigger', startDelay: 1, repeatInterval: 10000
		//cron name: 'syncTrigger', cronExpression: "0 45 10 1/1 * ? *"
    }
    def group = "CronJobs"
    def description = "Synchronize local db with couch db"
    def concurrent = false

    def couchSynchronizerService
    def domainClasses = [ Asset.class]

    def execute() {
		try {
			def settings = CouchSynchronizerSettings.findOrSaveById(1)
			println "RUN -> lastSync=$settings.lastSync"
			def now = new Date()
			def since = settings.lastSync
			if (!since) {
				since = new Date(0)
			}

			domainClasses.each { domainClass ->
				domainClass.findAllByLastUpdatedBetween(since, now)?.each { domain ->
					couchSynchronizerService.update(domain)
				}
			}
/*
			User.findAllByLastUpdatedBetween(since, now)?.each {
				couchSynchronizerService.updateUsers(it)
			}

			Asset.findAllByLastUpdatedBetween(since, now)?.each {
				println "---> changed asset: " + it
			}
*/
			settings.lastSync = now
			settings.save()
		}
		catch(Exception e) {
		    e.printStackTrace()
		}
    }
}
