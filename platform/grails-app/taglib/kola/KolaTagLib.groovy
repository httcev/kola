package kola

import grails.transaction.Transactional

class KolaTagLib {
	def springSecurityService
    static defaultEncodeAs = [taglib:'html']
    static namespace = "kola"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def displayName = { attrs ->
        // TODO: storing the user in the session is bad. this is a workaround for the problem that springSecurityService.currentUser is null in case of a webflow. loading it from DB is not possible due to "org.hibernate.HibernateException: No Session found for current thread"
        if (!session.blubber) {
            session.blubber = springSecurityService.currentUser
        }
        try{
            def user = session.blubber
            out << user?.profile?.displayName
        }catch(Throwable e) {
            log.error e
        }
    }
}
