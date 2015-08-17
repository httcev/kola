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
}
