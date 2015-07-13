import kola.Asset
import kola.User
import kola.Role
import kola.UserRole

class BootStrap {
	def repoDir

    def init = { servletContext ->
    	if (!repoDir.exists()) {
    		repoDir.mkdirs()
    	}

    	def asset = new Asset(name:"Asset 1", description:"Huhu sfsdf", mimeType:"text/plain", content:"Das ist ein Text!" as byte[])
    	if (!asset.save(true)) {
			asset.errors.allErrors.each { println it }
    	}

        if (Role.count() == 0) {
            def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
            def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

            def user1 = new User(username:"tittel", password:"tittel", displayName:"Stephan Tittel", company:"httc e.V.", department:"Knowledge & Educational Technologies", phone:"+49615116882", mobile:"+4915114474556", email:"stephan.tittel@kom.tu-darmstadt.de").save(flush: true)
            def user2 = new User(username:"admin", password:"admin", displayName:"Admin").save(flush: true)
            
            UserRole.create(user1, userRole, true)
            UserRole.create(user2, userRole, true)
            UserRole.create(user2, adminRole, true)
        }
    }
    def destroy = {
    }
}
