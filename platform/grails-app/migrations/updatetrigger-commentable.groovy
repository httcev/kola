databaseChangeLog = {
  changeSet(author: "tittel (generated)", id: "1574425794065-4") {
		addColumn(tableName: "commentable") {
			column(name: "update_trigger", type: "bigint")
		}
	}
}
