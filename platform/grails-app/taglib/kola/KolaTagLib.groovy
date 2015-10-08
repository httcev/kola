package kola

import org.pegdown.PegDownProcessor
import org.pegdown.LinkRenderer
import org.pegdown.ast.RefLinkNode
import org.pegdown.ast.AutoLinkNode
import org.pegdown.ast.ExpLinkNode
import org.pegdown.ast.MailLinkNode
import org.pegdown.ast.WikiLinkNode


class KolaTagLib {
	def springSecurityService
    static defaultEncodeAs = [taglib:'none']
    static namespace = "kola"
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def displayName = { attrs ->
        // TODO: storing the user in the session is bad. this is a workaround for the problem that springSecurityService.currentUser is null in case of a webflow. loading it from DB is not possible due to "org.hibernate.HibernateException: No Session found for current thread"
        if (!session.blubber) {
            session.blubber = springSecurityService.currentUser?.profile?.displayName
        }
        try{
            out << session.blubber
        }catch(Throwable e) {
            log.error e
        }
    }

    /* Custom LinkRender for adding target="_blank" attribute to markdown links */
    static linkRenderer = new LinkRenderer() {
        @Override
        LinkRenderer.Rendering render(ExpLinkNode node, String text)  {
            def result = super.render(node, text)
            result.attributes.add(new LinkRenderer.Attribute("target", "_blank"))
            return result
        }
    }

    def markdown = { attrs, body ->
        out << "<div class='markdown clearfix'>" + new PegDownProcessor().markdownToHtml(body().toString(), linkRenderer) + "</div>"
    }
}
