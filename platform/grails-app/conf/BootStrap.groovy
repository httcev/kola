import de.httc.plugins.user.User
import de.httc.plugins.user.Role
import de.httc.plugins.user.UserRole
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent
import kola.Task
import kola.TaskStep
import kola.ReflectionQuestion
import kola.Settings

class BootStrap {
	def repoDir
    def grailsApplication

    def init = { servletContext ->
    	if (!repoDir.exists()) {
    		repoDir.mkdirs()
    	}
        if (!Settings.getSettings()) {
            def settings = new Settings()
            if (!settings.save(true)) {
                settings.errors.allErrors.each {
                    println it
                }
            }
        }
        // cache if terms of use is set
        grailsApplication.config.kola.termsOfUseExisting = Settings.getSettings().termsOfUse?.length() > 0

        if (Role.count() == 0) {
            def adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true)
            def repositoryAdminRole = new Role(authority: 'ROLE_REPOSITORY_ADMIN').save(flush: true)
            def taskTemplateCreatorRole = new Role(authority: 'ROLE_TASK_TEMPLATE_CREATOR').save(flush: true)
            def reflectionQuestionCreatorRole = new Role(authority: 'ROLE_REFLECTION_QUESTION_CREATOR').save(flush: true)
            def teacherRole = new Role(authority: 'ROLE_TEACHER').save(flush: true)
            assert Role.count() == 5

            def adminUser = new User(username:"admin", password:"admin", email:"stephan.tittel@httc.de", profile:[firstName:"User", lastName:"Admin", company:"KOLA"]).save(flush: true)
            assert User.count() == 1

            UserRole.create(adminUser, adminRole, true)
            assert UserRole.count() == 1
        }
        if (ReflectionQuestion.count() == 0) {
/*            
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
*/
            new ReflectionQuestion(name:"Was ist mir gut gelungen?", autoLink:true).save(true)
            new ReflectionQuestion(name:"Was ist mir schwer gefallen (z.B. technisch oder fachlich)?", autoLink:true).save(true)
            new ReflectionQuestion(name:"Was würde ich beim nächsten Mal besser oder anders machen?", autoLink:true).save(true)
            new ReflectionQuestion(name:"Welche Fragen sind mir noch offen geblieben?", autoLink:true).save(true)
            new ReflectionQuestion(name:"Welche Fragen habe ich bereits zu den anstehenden Aufträgen?", autoLink:true).save(true)

            new ReflectionQuestion(name:"Zu welchem Thema wünsche ich mir noch Erklärungen?").save(true)
            new ReflectionQuestion(name:"Welche neuen Tätigkeiten würde ich gerne noch kennenlernen?").save(true)
            new ReflectionQuestion(name:"Welche Probleme/Störungen sind bei der Arbeit entstanden? Wie bin ich vorgegangen um die Probleme zu lösen?").save(true)
            new ReflectionQuestion(name:"Was habe ich bei diesem Auftrag neu gelernt? Was kann ich jetzt besser?").save(true)
            new ReflectionQuestion(name:"Welche Aufgaben waren interessant und würde ich gerne vertiefen?").save(true)

            assert ReflectionQuestion.count() == 10
        }

        environments {
            development {
                if (Task.count() == 0) {
                    def testUser = new User(username:"tittel", password:"tittel", email:"stephan.tittel@kom.tu-darmstadt.de", profile:[firstName:"Stephan", lastName:"Tittel", company:"httc e.V.", phone:"+49615116882", mobile:"+4915114474556"]).save(flush: true)

                    def numAssets = 3
                    for (int i=0; i<numAssets; i++) {
                        new Asset(name:"Asset $i", typeLabel:"learning-resource", mimeType:"text/plain", content:new AssetContent(data:"Das ist ein Text! $i" as byte[]), props:[_description:"$i Huhu".toString()]).save(true)
                    }
                    assert Asset.count() == numAssets

                    def numTaskTemplates = 2
                    def description = "### Abschnitt 1\n\n1. Aufzählungstext 1\n1. Aufzählungstext 2\n1. Aufzählungstext 3\n\n### Abschnitt 2\n\n- Aufzählungstext 1\n- Aufzählungstext 2\n- Aufzählungstext 3\n\n**Fett**\n_Kursiv_\n[Link](http://www.example.com)"
                    for (int i=0; i<numTaskTemplates; i++) {
                        def task = new Task(name:"Example Task Template $i", description:description, creator:testUser, isTemplate:true)
                        task.addToSteps(new TaskStep(name:"Step 1 example", description:description))
                        task.addToSteps(new TaskStep(name:"Step 2 example", description:description))
                        if (!task.save(true)) {
                            task.errors.allErrors.each { println it }
                        }
                    }
                    assert Task.count() == numTaskTemplates

                    def numTasks = 2
                    for (int i=0; i<numTasks; i++) {
                        new Task(name:"Example Task $i", description:description, creator:testUser).save(true)
                    }
                    assert Task.count() == numTaskTemplates + numTasks
                }
            }
        }

        grails.converters.JSON.registerObjectMarshaller(User) {
            def doc = [:]
            doc.id = it.id
            doc.username = it.username
            doc.email = it.email
            doc.displayName = it.profile?.displayName
            doc.company = it.profile?.company
            doc.phone = it.profile?.phone
            doc.mobile = it.profile?.mobile
            if (it.profile?.photo?.length > 0) {
                doc.photo = it.profile.photo.encodeBase64().toString()
            }
            return [id:it.id, doc:doc]
        }
        
        def _exported = ["name", "props", "mimeType", "type", "deleted"]
        def _referenced = ["creator"]
        grails.converters.JSON.registerObjectMarshaller(Asset) {
            def doc = asset.properties.findAll { k, v ->
                k in _exported
            }
            _referenced.each {
                if (asset."$it" instanceof List) {
                    doc."$it" = asset."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = asset."$it"?.id
                }
            }
            doc.id = asset.id
            return [id:asset.id, doc:doc]
        }
    }
    def destroy = {
    }
}
