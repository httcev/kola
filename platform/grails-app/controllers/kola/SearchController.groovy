package kola
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])
class SearchController {
	def elasticSearchService

    def index() {
		def highlighter = {
			field 'name'
			field 'description'
			field 'indexText'
			preTags '<strong>'
  			postTags '</strong>'
		}
		def results = elasticSearchService.search("${params.q}", [highlight: highlighter])    	
		println results
		
		[results:results]
    }
}
