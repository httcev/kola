package kola

class Profile {

	static constraints = {
		displayName blank: false, unique: true
		company nullable: true
		phone nullable: true
		mobile nullable: true
		// photo max size 500 kb
		photo maxSize: 1024 * 500, nullable: true
	}

    User user
   	String displayName
    String company
    String phone
    String mobile
    byte[] photo
}
