package kola
import org.springframework.security.access.annotation.Secured
import org.apache.lucene.queryparser.classic.QueryParser
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;

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
		}.findAll() // the last findAll() removes null values from the "types" list
		if (types) {
			options.types = types
		}
		println "--- options=" + options
		//def query = params.q ? QueryParser.escape(params.q) : null
		//println "q=" + query
//		def results = elasticSearchService.search("${params.q}", [highlight: highlighter, size:params.max, from:params.offset])    	

/*
		def esQuery = {
            filtered {
                query {
                    if (params.q) {
                        query_string {
                            //fields = ["venue.name","artists.name"]
                            query = params.q.encodeAsElasticSearchQuery()
                            default_operator = "AND"
                        }
                    } else {
                        match_all {}
                    }
                }
                filter {
                    if (params.subType) {
                        //geo_distance("venue.coords":coords,distance:"20mi")
                    } else {
                        match_all {}
                    }
                }
            }
        }
*/
//        def results = elasticSearchServic.search(esQuery, [highlight: highlighter, size:params.max, from:params.offset])    	
		def query = params.q ? escapeQuery(params.q) : null
println "---- HERE -> query=" + query

        def results = null
        if (params.q) {
	        results = elasticSearchService.search([searchType:'dfs_query_and_fetch', highlight: highlighter, size:params.max, from:params.offset]) {
			  bool {
			      must {
			          query_string(query: query)
			      }
			      if (params.subType) {
			          must {
			          	println "--- setting subType=" + params.subType
			              term(subType: params.subType)
			          }
			      }
			  }
			}
		}
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
