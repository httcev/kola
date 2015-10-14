package kola

class Profile {

	static constraints = {
		displayName blank: false, unique: true
		company nullable: true
		phone nullable: true
		mobile nullable: true
        lastUpdated nullable: true // needed for downward compatibility
		// photo max size 500 kb
		photo maxSize: 1024 * 500, nullable: true
	}

    User user
   	String displayName
    String company
    String phone
    String mobile
    byte[] photo
    Date lastUpdated

    static _exported = ["displayName", "company", "phone", "mobile", "photo"]
    static {
        grails.converters.JSON.registerObjectMarshaller(Profile) {
            def result = it.properties.findAll { k, v ->
                k in _exported
            }
            result.id = it.id
            return result
        }
    }
}
