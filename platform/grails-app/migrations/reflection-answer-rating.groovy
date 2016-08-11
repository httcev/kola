databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1470914429942-10") {
		dropNotNullConstraint(columnDataType: "longvarchar", columnName: "TEXT", tableName: "REFLECTION_ANSWER")
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-2") {
		addColumn(tableName: "reflection_answer") {
			column(name: "rating", type: "varchar(255)")
		}
	}
}
