import kola.Asset
import kola.User
import kola.Role
import kola.UserRole
import kola.Task
import kola.TaskTemplate

class BootStrap {
	def repoDir

    def init = { servletContext ->
    	if (!repoDir.exists()) {
    		repoDir.mkdirs()
    	}

        if (Role.count() == 0) {
            def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
            def repositoryAdminRole = new Role(authority: 'ROLE_REPOSITORY_ADMIN').save(flush: true)
            def taskTemplateCreatorRole = new Role(authority: 'ROLE_TASK_TEMPLATE_CREATOR').save(flush: true)
            assert Role.count() == 3

            def adminUser = new User(username:"admin", password:"admin", email:"stephan.tittel@httc.de", profile:[displayName:"Admin User", company:"KOLA"]).save(flush: true)
            assert User.count() == 1

            UserRole.create(adminUser, adminRole, true)
            assert UserRole.count() == 1
        }

        environments {
            development {
                def testUser = new User(username:"tittel", password:"tittel", email:"stephan.tittel@kom.tu-darmstadt.de", profile:[displayName:"Stephan Tittel", company:"httc e.V.", phone:"+49615116882", mobile:"+4915114474556"]).save(flush: true)

                def numAssets = 20
                for (i in 1..numAssets) {
                    new Asset(name:"Asset $i", description:"$i Huhu sfsdflkfj lsjkdfl sjdflk sjldkfj slkdfj lskdjf lskjdf lkjlekrjtlwke4l rlwem rlwekrm wlekrmw elkrmwlekmr lwekrmwlekrmwlerm welkrmwlekmr wlekrmwlekr lwkemr lwkemr lwekrmwlkermw lekmr weklrm wermll23mr2l 3km rlk3mr lwekm welrkm  $i", mimeType:"text/plain", content:"Das ist ein Text! $i" as byte[]).save(true)
                }
                assert Asset.count() == numAssets

                def numTaskTemplates = 15
                for (i in 1..numTaskTemplates) {
                    new TaskTemplate(name:"Example Task Template $i", creator:testUser).save(true)
                }
                assert TaskTemplate.count() == numTaskTemplates

                def numTasks = 15
                for (i in 1..numTasks) {
                    new Task(name:"Example Task $i", creator:testUser).save(true)
                }
                assert Task.count() == numTasks
            }
        }
    }
    def destroy = {
    }
}
