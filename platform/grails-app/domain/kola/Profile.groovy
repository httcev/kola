package kola

class Profile {

	static constraints = {
		displayName blank: false, unique: true
		company nullable: true
		department nullable: true
		phone nullable: true
		mobile nullable: true
		email nullable: true
		// photo max size 500 kb
		photo maxSize: 1024 * 500, nullable: true
	}

    User user
   	String displayName
    String company
    String department
    String phone
    String mobile
    String email
    byte[] photo
}
