package kola

class KolaTagLib {
	def springSecurityService
    static defaultEncodeAs = [taglib:'html']
    static namespace = "kola"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def displayName = { attrs ->
        def user = springSecurityService.currentUser as User
        out << user?.profile?.displayName
    }
}
