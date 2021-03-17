databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1600248635781-1") {
		addColumn(tableName: "question_reference") {
			column(name: "expires", type: "timestamp")
		}
	}
}
