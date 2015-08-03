package kola
import org.springframework.security.access.annotation.Secured

@Secured(['IS_AUTHENTICATED_FULLY'])
class SearchController {
	def elasticSearchService

    def index() {
        params.max = Math.min(params.max ? (params.max as int) : 10, 100)
        params.offset = params.offset ? (params.offset as int) : 0

		def highlighter = {
			field 'name'
			field 'description'
			field 'indexText'
			preTags '<strong>'
  			postTags '</strong>'
		}
		def results = elasticSearchService.search("${params.q}", [highlight: highlighter, size:params.max, from:params.offset])    	
		//println results
		/*
		if (request.xhr) {
			render (template:"searchResult", model:[results:results])
		}
		else {
			[results:results]
		}
		*/
		//respond results
		[results:results]
    }
}
