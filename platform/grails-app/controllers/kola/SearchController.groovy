package kola

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
		def results = elasticSearchService.search("${params.query}", [highlight: highlighter])    	
		println results
		
		[results:results]
    }
}
