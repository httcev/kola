import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import org.slf4j.MDC

class UsageTrackingFilters {
    private static final Log LOG = LogFactory.getLog('usagetracking')
    def springSecurityService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                MDC.put('user', springSecurityService.principal?.username)
                MDC.put('method', request.method)
                def userAgent = request.getHeader("User-Agent")?.replaceAll(",", ";")
                if (!userAgent) {
                    userAgent = "unknown"
                }
                MDC.put('userAgent', userAgent)
                String sessionId = RequestContextHolder.getRequestAttributes()?.getSessionId()
                if(sessionId){
                    MDC.put('sessionId', sessionId)
                }
                LOG.info(params.controller + (params.action ? ("/" + params.action + (params.id ? ("/" + params.id) : "")) : ""))
            }
            after = {
            }
            afterView = {
                MDC.remove('sessionId')
                MDC.remove('user')
                MDC.remove('method')
                MDC.remove('userAgent')
            }
        }
    }
}
