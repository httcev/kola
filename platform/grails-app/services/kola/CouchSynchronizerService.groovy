package kola

import grails.transaction.Transactional
import org.lightcouch.CouchDbClient
import org.lightcouch.NoDocumentException
import com.google.gson.GsonBuilder

@Transactional
class CouchSynchronizerService {
    def couchdbProtocol
    def couchdbHost
    def couchdbPort
    def couchdbAdminUser
    def couchdbAdminPass
    def couchdbDatabase

    def listenForChanges() {

    }

    def updateUsers(users) {
    	def client = new CouchDbClient("_users", false, couchdbProtocol, couchdbHost, couchdbPort as Integer, couchdbAdminUser, couchdbAdminPass)
    	users?.each {
    		println "--- updating user $it.username"

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

    def update(domain) {
        def db = couchdbDatabase
        def id = domain.class.simpleName.toLowerCase() + ":" + domain.id
        if (domain instanceof User) {
            db = "_users"
            id = "org.couchdb.user:" + domain.username
        }
        println "--- ID=" + id
        println "--- updating domain $domain"

        def client = createClient(db)
        try {

            println client.save(domain)
            /*
            def stream
            try {
                stream = client.find(id)
                println "--- exsiting domain: $id"
            }
            catch(NoDocumentException e) {
                println "--- creating new domain"
                //CouchUser couchUser = new CouchUser().updateFrom(it)
                println client.save(domain)
            }
            finally {
                stream?.close()
            }
            */
        }
        finally {
            //println client.context().getAllDbs()
            client.shutdown()
        }
    }

    def createClient(db) {
        def client = new CouchDbClient(db, false, couchdbProtocol, couchdbHost, couchdbPort as Integer, couchdbAdminUser, couchdbAdminPass)
        GsonBuilder gsonBuilder = new GsonBuilder()
         .registerTypeAdapter(Asset.class, new AssetTypeAdapter())
         .enableComplexMapKeySerialization()
         .serializeNulls()
         //.setDateFormat(java.text.DateFormat.LONG)
         //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
         .setPrettyPrinting()
         .setVersion(1.0)
         
         client.setGsonBuilder(gsonBuilder)
         return client
    }

    class CouchDoc {
    	String _id
    	String _rev
    }

    class CouchUser extends CouchDoc {
    	String name
    	String type = "user"
    	def roles = ["empty"]
    	//String password_scheme = "pbkdf2"
    	//String salt
    	//String derived_key
    	//int iterations

    	CouchUser updateFrom(User user) {
    		_id = "org.couchdb.user:$user.username"
    		name = user.username

			//def tokens = user.password.split(':')
			//iterations = tokens[0] as int
			//derived_key = tokens[2]
			//salt = tokens[1]
			//1000:f048821b6476851379042bea4de6928ef69198b558ae4a60:a2c86c353e2e8c8bcaef9bea23fa61540907ab33607fdeea
	     //-pbkdf2-7f2f26be6040f1e1409045382a75712f812e1c30,603c579b7b887fb06172debd690e087d,10
	     	return this
    	}
    }
}
