databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1470913842590-2") {
		addColumn(tableName: "setting") {
			column(name: "exported", type: "boolean")
		}
	}
	changeSet(author: "tittel (generated)", id: "1470913842590-3") {
		addColumn(tableName: "setting") {
			column(name: "last_updated", type: "timestamp")
		}
	}
}
