package kola

class ReflectionQuestion {

    static constraints = {
    	name blank:false
    }
    static mapping = {
        name type:"text"
    }
	static searchable = {
		all = [analyzer: 'german']
        only = ['name', 'deleted']
    }

    String name
    Date lastUpdated
    boolean deleted
    boolean autoLink

    static _exported = ["name", "deleted", "autoLink"]
    static {
        grails.converters.JSON.registerObjectMarshaller(ReflectionQuestion) {
            def doc = it.properties.findAll { k, v ->
                k in _exported
            }
            doc.id = it.id
            return [id:it.id, doc:doc]
        }
    }
}
