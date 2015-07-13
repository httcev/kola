package kola

import grails.transaction.Transactional
import org.lightcouch.CouchDbClient
import org.lightcouch.NoDocumentException

@Transactional
class CouchSynchronizerService {
    def couchdbProtocol
    def couchdbHost
    def couchdbPort
    def couchdbAdminUser
    def couchdbAdminPass

    def listenForChanges() {

    }

    def updateUsers(users) {
    	def client = new CouchDbClient("_users", false, couchdbProtocol, couchdbHost, couchdbPort as Integer, couchdbAdminUser, couchdbAdminPass)
    	users?.each {
    		println "--- updating user $it.username"
			def tokens = it.password.split(':')
			def rounds = tokens[0]
			def hash = tokens[1]
			def salt = tokens[2]
			def couchPass = "-pbkdf2-$hash,$salt,$rounds"
			//1000:f048821b6476851379042bea4de6928ef69198b558ae4a60:a2c86c353e2e8c8bcaef9bea23fa61540907ab33607fdeea
	     //-pbkdf2-7f2f26be6040f1e1409045382a75712f812e1c30,603c579b7b887fb06172debd690e087d,10
			println it.password
			println couchPass

			try {
				CouchUser couchUser = client.find(CouchUser.class, "org.couchdb.user:" + it.username)
				println "--- exsiting couchUser: $couchUser"
			}
			catch(NoDocumentException e) {
				println "--- creating new couch user"
				CouchUser couchUser = new CouchUser().updateFrom(it)
				println client.save(couchUser)
			}

    	}
    	println client.context().getAllDbs()
    	client.shutdown()
    }

    class CouchDoc {
    	String _id
    	String _rev
    }

    class CouchUser extends CouchDoc {
    	String name
    	String type = "user"
    	def roles = ["empty"]
    	String password_scheme = "pbkdf2"
    	String salt
    	String derived_key
    	int iterations

    	CouchUser updateFrom(User user) {
    		_id = "org.couchdb.user:$user.username"
    		name = user.username

			def tokens = user.password.split(':')
			iterations = tokens[0] as int
			derived_key = tokens[2]
			salt = tokens[1]
			//1000:f048821b6476851379042bea4de6928ef69198b558ae4a60:a2c86c353e2e8c8bcaef9bea23fa61540907ab33607fdeea
	     //-pbkdf2-7f2f26be6040f1e1409045382a75712f812e1c30,603c579b7b887fb06172debd690e087d,10
	     	return this
    	}
    }
}
