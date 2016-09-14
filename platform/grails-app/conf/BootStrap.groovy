import de.httc.plugins.user.User
import de.httc.plugins.user.Role
import de.httc.plugins.user.UserRole
import de.httc.plugins.repository.Asset
import de.httc.plugins.repository.AssetContent
import de.httc.plugins.taxonomy.Taxonomy
import de.httc.plugins.taxonomy.TaxonomyTerm
import de.httc.plugins.qaa.Question
import de.httc.plugins.qaa.Answer
import de.httc.plugins.qaa.Comment
import de.httc.plugins.common.Setting
import kola.Task
import kola.TaskStep
import kola.ReflectionQuestion

class BootStrap {
	def repoDir
	def grailsApplication

	def init = { servletContext ->
		if (!repoDir.exists()) {
			repoDir.mkdirs()
		}
		if (!Setting.getValue("welcomeHeader")) {
			new Setting(key:"welcomeHeader", value:"Willkommen", required:true, multiline:false, prefix:"kola.settings", weight:1).save()
			new Setting(key:"welcomeBody", value:"Dies ist die KOLA Plattform.", required:false, multiline:true, prefix:"kola.settings", weight:2).save()
			new Setting(key:"termsOfUse", value:null, required:false, multiline:true, prefix:"kola", weight:3).save(true)
//			assert Setting.count() == 3 // removed assert since plugins can create own settings
		}
		// cache if terms of use is set
		grailsApplication.config.kola.termsOfUseExisting = Setting.getValue("termsOfUse")?.length() > 0

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
			new ReflectionQuestion(name:"Was habe ich bei diesem Arbeitsauftrag neu gelernt? Was kann ich jetzt besser?").save()
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

					def typeTaxonomy = new Taxonomy(label:"taskType")
					def companyTypeTerm = new TaxonomyTerm(label:"Betrieb")
					typeTaxonomy.addToTerms(companyTypeTerm)
					typeTaxonomy.addToTerms(new TaxonomyTerm(label:"Schule"))
					typeTaxonomy.save(true)
					assert Taxonomy.count() == 1
					assert TaxonomyTerm.count() == 2

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
						def task = new Task(name:"Example Task $i", description:description, creator:User.findByUsername("admin"), assignee:testUser, type:companyTypeTerm)
						task.addToSteps(new TaskStep(name:"Step 1 example", description:description))
						task.addToSteps(new TaskStep(name:"Step 2 example", description:description))
						if (!task.save(true)) {
							task.errors.allErrors.each { println it }
						}
						def question = new Question(title:"Ich habe eine Frage $i", text:"Was ist grün und hüpft von Baum zu Baum?", creator:testUser, reference:task)
						def answer = new Answer(text:"Weiss nicht! Ein Frosch?", creator:testUser)
						answer.addToComments(new Comment(text:"Antwortkommentar...", creator:testUser))
						question.addToAnswers(answer)
						question.addToComments(new Comment(text:"Fragenkommentar 1...", creator:testUser))
						question.addToComments(new Comment(text:"Fragenkommentar 2...", creator:testUser))
						if (!question.save(true)) {
							question.errors.allErrors.each { println it }
						}
					}

					assert Task.count() == numTaskTemplates + numTasks
					assert Question.count() == numTasks
					assert Answer.count() == numTasks
					assert Comment.count() == numTasks * 3
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
				if (asset."$it" instanceof Collection) {
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
				k in ["title", "text", "deleted", "lastUpdated", "dateCreated", "metadata", "rated", "rating"]
			}
			["attachments", "creator", "acceptedAnswer", "reference"].each {
				if (question."$it" instanceof Collection) {
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
				k in ["text", "deleted", "lastUpdated", "dateCreated", "rated", "rating"]
			}
			["attachments", "creator", "question"].each {
				if (answer."$it" instanceof Collection) {
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
				k in ["text", "deleted", "lastUpdated", "dateCreated"]
			}
			["creator", "reference"].each {
				if (comment."$it" instanceof Collection) {
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
		// REFLECTION ANSWER RATINGS
		grails.converters.JSON.registerObjectMarshaller(kola.ReflectionAnswer$Rating) { rating ->
			return rating.toString()
		}
	}
	def destroy = {
	}
}
