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
        only = ['name']
    }

    String name

    static _exported = ["name"]
    static {
        grails.converters.JSON.registerObjectMarshaller(ReflectionQuestion) {
            def result = it.properties.findAll { k, v ->
                k in _exported
            }
            result.id = it.id
            return result
        }
    }
}
