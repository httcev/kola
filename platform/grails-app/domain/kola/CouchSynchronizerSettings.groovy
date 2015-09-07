package kola

class CouchSynchronizerSettings {

    static constraints = {
		lastSync nullable: true
    }

    Date lastSync
}
