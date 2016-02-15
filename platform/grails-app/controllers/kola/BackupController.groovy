package kola

import grails.converters.XML
import org.springframework.security.access.annotation.Secured
import grails.transaction.Transactional
import groovy.xml.*
import java.text.SimpleDateFormat
import java.text.DateFormat
import de.httc.plugins.repository.Asset

@Secured(['ROLE_ADMIN'])
@Transactional
class BackupController {
	static allowedMethods = [index:"GET", export:"GET", restore:"POST"]
	static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    static DOMAIN_CLASS_MAPPING = ["asset":Asset, "taskStep":TaskStep, "taskDocumentation":TaskDocumentation, "reflectionAnswer":ReflectionAnswer, "task":Task]
    def springSecurityService
    def grailsApplication

    def index() {

    }

    def export() {
    	def data = [:]
    	grailsApplication.getArtefacts("Domain")*.clazz.each {
    		data[it.name] = it.list()
    	}
    	render data as XML
    }

    def restore() {
        def file = request.getFile("file")
        if (file && !file.empty) {
            def text = new String(file.bytes)
            //def map = paramsCreated(text)
//            println map
            
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
//                        println "--- id -> " + id
                        def o = domainClass.newInstance()
                        def map = paramsCreated(XmlUtil.serialize(row))
/*
                        row.children().each { child ->
                            println "--- child " + XmlUtil.serialize(child)
                            println "--- name=" + child.name
                        }
                        //o.id = id
*/                      
                        if (map.content) {
                            map.content = map.content?.decodeBase64()
                        }
//                        println map

                        o.properties = map

//                        println "----------------------------"
//                        println o.properties
/*
                        if (!o.save()) {
                            o.errors.allErrors.each { println it }
                            throw new RuntimeException("backup failed")
                        }
                        else {
                            println "--- SAVED -> " + o
                        }
  */
                    }
                }
                else {
                    log.error "Couldn't load domain class '${domainClassName}'. Ignoring all objects from backup with this class."
                }
            }

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
