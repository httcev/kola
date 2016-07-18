package kola

import grails.converters.XML
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import groovy.xml.*
import java.text.SimpleDateFormat
import java.text.DateFormat
import de.httc.plugins.repository.Asset
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import java.util.zip.ZipInputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.ByteArrayOutputStream

@Secured(['ROLE_ADMIN'])
@Transactional(readOnly = true)
class BackupController {
	static allowedMethods = [index:"GET", export:"GET", restore:"POST"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "task":Task]
	def springSecurityService
	def grailsApplication

	def index() {

	}

	def export() {
		response.setContentType("application/octet-stream")
		response.setHeader("content-disposition", "attachment;filename='backup.zip'")
		ZipOutputStream zip = new ZipOutputStream(response.outputStream)
		def counter = 0
		grailsApplication.getArtefacts("Domain")*.clazz.each { domainClass ->
			zip.putNextEntry(new ZipEntry("${domainClass.name}/"))
			domainClass.list().each { domainInstance ->
				zip.putNextEntry(new ZipEntry("${domainClass.name}/${counter++}"))
				zip.write(domainInstance.encodeAsXML().toString().getBytes("UTF-8"));
			}
		}
		zip.close();
	}

	@Transactional
	def restore() {
		def file = request.getFile("file")
		if (file && !file.empty) {
			def zipInputStream = new ZipInputStream(file.inputStream)
		//	Enumeration<? extends ZipEntry> entries = zipFile.entries()
			def zipEntry
			def buffer = new byte[128]
			int read
			while((zipEntry = zipInputStream.nextEntry) != null) {
//				ZipEntry entry = entries.nextElement()
				if (!zipEntry.isDirectory()) {
					def domainClassName = zipEntry.name.split("/")[0]
					println "domain class name: " + domainClassName
					def domainClass = grailsApplication.getDomainClass(domainClassName).clazz
					if (!domainClass.equals(de.httc.plugins.user.UserRole)) {
						def bos = new ByteArrayOutputStream()
						while ((read = zipInputStream.read(buffer)) > -1) {
							bos.write(buffer, 0, read)
						}
						def text = new String(bos.toByteArray(), "UTF-8")
						bos.close()
						def map = paramsCreated(text)
						println map

						def o
						if (map.id) {
							println "trying to load id ${map.id}"
							o = domainClass.get(map.id)
							println "loaded o=${o}"
							o.properties = map
						}
						if (!o) {
							 o = domainClass.newInstance(map)
						 }
						print o
						if (!o.validate()) {
							o.errors.allErrors.each { println it }
							throw new RuntimeException("backup failed")
						}
						else {
							o.save()
							println "--- SAVED -> " + o
						}
					}
				}
			}
/*
			def text = new String(file.bytes)
			//def map = paramsCreated(text)
//			println map

			def xml = XML.parse(text)
			xml.entry?.each { domain ->
				def domainClassName = domain.attributes().key
				println "----------------------------"
				println grailsApplication.getDomainClass(domainClassName).properties.findAll {
					it.association == true
				}
				def domainClass = grailsApplication.getDomainClass(domainClassName)?.clazz
				//println "--- class -> " + domainClass
				if (domainClass) {
					domain.children()?.each { row ->
						def id = row.@id.toString()
//						println "--- id -> " + id
						def o = domainClass.newInstance()
						def map = paramsCreated(XmlUtil.serialize(row))
/*
						row.children().each { child ->
							println "--- child " + XmlUtil.serialize(child)
							println "--- name=" + child.name
						}
						//o.id = id
*/
/*
						if (map.content) {
							map.content = map.content?.decodeBase64()
						}
//						println map

						o.properties = map

//						println "----------------------------"
//						println o.properties
*/
/*
						if (!o.save()) {
							o.errors.allErrors.each { println it }
							throw new RuntimeException("backup failed")
						}
						else {
							println "--- SAVED -> " + o
						}
					}
				}
				else {
					log.error "Couldn't load domain class '${domainClassName}'. Ignoring all objects from backup with this class."
				}
			}
			*/

		}
	}

	private def paramsCreated(text) {
		try {
			def xml = XML.parse(text)
			if (xml != null) {
				def name =  xml.name()
				def map = [:]
				def id = xml.@id.text()
				if (id) {
					map['id'] = id
				}
				//params[name] = map
				populateParamsFromXML(xml, map)
				/*
				def target = [:]
				super.createFlattenedKeys(map, map, target)
				for (entry in target) {
					if (!map[entry.key]) {
						map[entry.key] = entry.value
					}
				}
				*/
				return map
			}
		}
		catch (Exception e) {
			LOG.error "Error parsing XML: ${e.message}", e
		}
	}

	private populateParamsFromXML(xml, map) {
		for (child in xml.children()) {
			// one-to-ones have ids
			if (child.@id.text()) {
				map["${child.name()}.id"] = child.@id.text()
				def childMap = [:]
				map[child.name()] = childMap
				populateParamsFromXML(child, childMap)
			}
			else {
				map[child.name()] = child.text()
			}
		}
	}
}
