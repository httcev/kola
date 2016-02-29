import de.httc.plugins.user.User
import de.httc.plugins.user.Role
import de.httc.plugins.user.UserRole
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent
import de.httc.plugins.qaa.Question
import de.httc.plugins.qaa.Answer
import de.httc.plugins.qaa.Comment
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
            new ReflectionQuestion(name:"Was ist mir gut gelungen?", autoLink:true).save()
            new ReflectionQuestion(name:"Was ist mir schwer gefallen (z.B. technisch oder fachlich)?", autoLink:true).save()
            new ReflectionQuestion(name:"Was würde ich beim nächsten Mal besser oder anders machen?", autoLink:true).save()
            new ReflectionQuestion(name:"Welche Fragen sind mir noch offen geblieben?", autoLink:true).save()
            new ReflectionQuestion(name:"Welche Fragen habe ich bereits zu den anstehenden Aufträgen?", autoLink:true).save()

            new ReflectionQuestion(name:"Zu welchem Thema wünsche ich mir noch Erklärungen?").save()
            new ReflectionQuestion(name:"Welche neuen Tätigkeiten würde ich gerne noch kennenlernen?").save()
            new ReflectionQuestion(name:"Welche Probleme/Störungen sind bei der Arbeit entstanden? Wie bin ich vorgegangen um die Probleme zu lösen?").save()
            new ReflectionQuestion(name:"Was habe ich bei diesem Auftrag neu gelernt? Was kann ich jetzt besser?").save()
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
                        ReflectionQuestion.getAll().each {
                            task.addToReflectionQuestions(it)
                        }
                        if (!task.save(true)) {
                            task.errors.allErrors.each { println it }
                        }
                    }
                    assert Task.count() == numTaskTemplates

                    def numTasks = 2
                    for (int i=0; i<numTasks; i++) {
                        def task = new Task(name:"Example Task $i", description:description, creator:User.findByUsername("admin"), assignee:testUser).save(true)
                        task.addToSteps(new TaskStep(name:"Step 1 example", description:description))
                        task.addToSteps(new TaskStep(name:"Step 2 example", description:description))
                        new Question(title:"Ich habe eine Frage $i", text:"Was ist grün und hüpft von Baum zu Baum?", creator:testUser, reference:task).save(true)
                    }
                    assert Task.count() == numTaskTemplates + numTasks
                    assert Question.count() == numTasks
                }
            }
        }

        // USERS
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

        // ASSETS
        grails.converters.JSON.registerObjectMarshaller(Asset) { asset ->
            def doc = asset.properties.findAll { k, v ->
                k in ["name", "props", "mimeType", "url", "typeLabel", "deleted"]
            }
            ["creator"].each {
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

        // QUESTIONS
        grails.converters.JSON.registerObjectMarshaller(Question) { question ->
            def doc = question.properties.findAll { k, v ->
                k in ["title", "text", "deleted", "lastUpdated", "metadata", "rated", "rating"]
            }
            ["attachments", "answers", "comments", "creator", "acceptedAnswer", "reference"].each {
                if (question."$it" instanceof List) {
                    doc."$it" = question."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = question."$it"?.id
                }
            }
            doc.id = question.id
            return [id:question.id, doc:doc]
        }
        // ANSWERS
        grails.converters.JSON.registerObjectMarshaller(Answer) { answer ->
            def doc = answer.properties.findAll { k, v ->
                k in ["text", "deleted", "lastUpdated", "rated", "rating"]
            }
            ["attachments", "comments", "creator", "question"].each {
                if (answer."$it" instanceof List) {
                    doc."$it" = answer."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = answer."$it"?.id
                }
            }
            doc.id = answer.id
            return [id:answer.id, doc:doc]
        }
        // COMMENTS
        grails.converters.JSON.registerObjectMarshaller(Comment) { comment ->
            def doc = comment.properties.findAll { k, v ->
                k in ["text", "deleted", "lastUpdated"]
            }
            ["creator"].each {
                if (comment."$it" instanceof List) {
                    doc."$it" = comment."$it"?.collect {
                        it?.id
                    }
                }
                else {
                    doc."$it" = comment."$it"?.id
                }
            }
            doc.id = comment.id
            return [id:comment.id, doc:doc]
        }
    }
    def destroy = {
    }
}
