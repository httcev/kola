package kola

import grails.transaction.Transactional
import java.io.File
import java.io.OutputStream

import org.docx4j.XmlUtils
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl
import org.docx4j.convert.out.html.AbstractHtmlExporter
import org.docx4j.convert.out.html.AbstractHtmlExporter.HtmlSettings
import org.docx4j.convert.out.html.HtmlExporterNG2
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import  org.docx4j.openpackaging.io.SaveToZipFile

@Transactional(readOnly=true)
class TaskExportService {
	def groovyPageRenderer
	def pdfRenderingService

	def exportToWord(taskInstance, response) {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage()

		NumberingDefinitionsPart ndp = new NumberingDefinitionsPart()
		wordMLPackage.getMainDocumentPart().addTargetPart(ndp)
		ndp.unmarshalDefaultNumbering()

		XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(wordMLPackage)

		def contents = groovyPageRenderer.render(template:"/task/export", model:prepareExportModel(taskInstance))
		wordMLPackage.getMainDocumentPart().getContent().addAll(xHTMLImporter.convert(new StringReader(contents), null))
		println "........ CONTENTS=${contents}"

		def filename = createFilename(taskInstance, ".docx")
		response.contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
		response.setHeader("Content-disposition", "attachment;filename=${filename}")
		SaveToZipFile saver = new SaveToZipFile(wordMLPackage)
		saver.save(response.outputStream)
	}

	def exportToPdf(taskInstance, response) {
		def filename = createFilename(taskInstance, ".pdf")
		pdfRenderingService.render([template:"/task/export", model:prepareExportModel(taskInstance), filename:filename], response)
	}

	private def prepareExportModel(taskInstance) {
		def reflectionAnswers = [:]
		def taskDocumentations = [:]
		if (!taskInstance.isTemplate) {
			taskInstance.reflectionQuestions?.each { reflectionQuestion ->
				reflectionAnswers[reflectionQuestion.id] = ReflectionAnswer.findAllByTaskAndQuestionAndDeleted(taskInstance, reflectionQuestion, false)
			}

			def docs = TaskDocumentation.findAllByReferenceAndDeleted(taskInstance, false)
			if (docs) {
				docs.each { doc ->
					doc.properties.each {
						println it
					}
				}
				taskDocumentations[taskInstance.id] = docs
			}
			taskInstance.steps?.each { step ->
				docs = TaskDocumentation.findAllByReferenceAndDeleted(step, false)
				if (docs) {
					taskDocumentations[step.id] = docs
				}
			}
		}
		return ["task":taskInstance, "reflectionAnswers":reflectionAnswers, "taskDocumentations":taskDocumentations]
	}

	private def createFilename(taskInstance, suffix) {
		def filename = taskInstance.name?.replaceAll("\\W+", "_");
		if (!filename) {
			filename = "export"
		}
		filename += suffix
		return filename
	}
}
