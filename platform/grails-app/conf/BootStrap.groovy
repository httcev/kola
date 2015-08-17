import kola.Asset
import kola.User
import kola.Role
import kola.UserRole
import kola.Task
import kola.TaskStep
import kola.ReflectionQuestion

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
            def reflexionQuestionCreatorRole = new Role(authority: 'ROLE_REFLEXION_QUESTION_CREATOR').save(flush: true)
            assert Role.count() == 4

            def adminUser = new User(username:"admin", password:"admin", email:"stephan.tittel@httc.de", profile:[displayName:"Admin User", company:"KOLA"]).save(flush: true)
            assert User.count() == 1

            UserRole.create(adminUser, adminRole, true)
            assert UserRole.count() == 1
        }
        if (ReflectionQuestion.count() == 0) {
            new ReflectionQuestion(name:"Kam es bei der Durchführung der Arbeitshandlung zu Schwierigkeiten (technisch/fachlich)?").save(true)
            new ReflectionQuestion(name:"Wodurch kann die Funktionstüchtigkeit grundsätzlich beeinträchtigt werden?").save(true)
            new ReflectionQuestion(name:"Was kann bei einer erneuten Durchführung der verschiedenen Arbeitsschritte besser/anders durchgeführt werden?").save(true)
            new ReflectionQuestion(name:"Ist der Arbeitsschritt in der dafür vorgesehen Zeit durchgeführt worden?").save(true)
            new ReflectionQuestion(name:"Welcher Arbeitsschritt war am zeitintensivsten? Begründen Sie ihre Aussage.").save(true)
            new ReflectionQuestion(name:"Welches Funktionswissen/Vorwissen war für die Durchführung der Arbeiten notwendig?").save(true)
            new ReflectionQuestion(name:"Ist die Planung und Durchführung der Arbeitshandlung selbstständig erfolgt oder gab es Hilfestellungen von Meistern bzw. Gesellen?").save(true)
            new ReflectionQuestion(name:"Haben Sie die Arbeit alleine durchgeführt oder im Team?").save(true)
            new ReflectionQuestion(name:"Welche Rückmeldungen haben Sie vom Team vor Ort erhalten?").save(true)
            new ReflectionQuestion(name:"Welche Teilschritte des gesamten Kundenauftrags ging den durchgeführten Arbeiten voraus? Welche schließen sich an?").save(true)
            new ReflectionQuestion(name:"War die durchgeführte Arbeit eher über- oder unterfordernd? Begründen Sie Ihre Meinung.").save(true)
             assert ReflectionQuestion.count() == 11
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
                    def task = new Task(name:"Example Task Template $i", description:"Description for Example Task Template $i", creator:testUser, isTemplate:true)
                    task.addToSteps(new TaskStep(name:"Step 1 example", description:"Step 1 example description"))
                    task.addToSteps(new TaskStep(name:"Step 2 example", description:"Step 2 example description"))
                    task.save(true)
                }
                assert Task.count() == numTaskTemplates

                def numTasks = 15
                for (i in 1..numTasks) {
                    new Task(name:"Example Task $i", description:"Description for Example Task $i", creator:testUser).save(true)
                }
                assert Task.count() == numTaskTemplates + numTasks
            }
        }
    }
    def destroy = {
    }
}
