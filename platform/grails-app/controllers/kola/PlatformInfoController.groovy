package kola

import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_ADMIN'])
class PlatformInfoController {

    def index() { }
}
