package kola
import org.springframework.security.access.annotation.Secured
import org.apache.lucene.queryparser.classic.QueryParser
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;
import de.httc.plugins.repository.Asset

@Secured(['IS_AUTHENTICATED_REMEMBERED'])
class SearchController {
	def elasticSearchService
	def grailsApplication

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
		def options = [searchType:'dfs_query_and_fetch', highlight: highlighter, size:params.max, from:params.offset]
		// restrict search to specific index
		def indexName = grailsApplication.config.elasticSearch?.index?.name
		if (indexName) {
			// need to convert from GStringImpl to java String here, otherwise we'll get an error from elasticSearchService
			options.indices = indexName as String
		}
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

		def query = escapeQuery(params.q)
		def results = null
		if (query) {
			results = elasticSearchService.search(options) {
				bool {
					must {
						query_string(query:query, analyze_wildcard:true)
					}
					must {
						term(deleted:false)
					}
					if (params.typeLabel) {
						must {
							term(typeLabel: params.typeLabel)
						}
					}
					// exclude attachments from search result
					must_not {
						term(typeLabel:"attachment")
					}
				}
			}
		}
		[results:results]
	}

	protected String escapeQuery(String query) {

		//def escapedCharacters = Regexp.escape('\\+-&|!(){}[]^~*?:')
		//return query?.gsub(/([#{escaped_characters}])/, '\\\\\1')

		if ("*".equals(query)) {
			return query;
		}
		query = query?.trim()
		if (query) {
			return "*" + QueryParser.escape(query) + "*"
		}
		return null
	}
}
