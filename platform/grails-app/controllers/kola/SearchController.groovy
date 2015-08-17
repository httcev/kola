package kola
import org.springframework.security.access.annotation.Secured
import org.apache.lucene.queryparser.classic.QueryParser

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
		def options = [highlight: highlighter, size:params.max, from:params.offset]
		def types = params.list("type")?.unique(false).collect {
			switch (it) {
				case "asset":
					return Asset.class
				case "reflectionQuestion":
					return ReflectionQuestion.class
			}
		}.findAll()
		if (types) {

			options.types = types
		}
		println "--- options=" + options
		def query = params.q ? QueryParser.escape(params.q) : null
		println "q=" + query
//		def results = elasticSearchService.search("${params.q}", [highlight: highlighter, size:params.max, from:params.offset])    	
		def results = elasticSearchService.search(query, options)    	
		[results:results]
    }

    protected String escapeQuery(String query) {
    	/*
		def escapedCharacters = Regexp.escape('\\+-&|!(){}[]^~*?:')
		return query?.gsub(/([#{escaped_characters}])/, '\\\\\1')
		*/
		return QueryParser.escape(query)
	}
}
