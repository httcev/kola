databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1574327423686-4") {
		createTable(tableName: "setting_possible_values") {
			column(name: "setting_id", type: "bigint")

			column(name: "possible_values_string", type: "varchar(255)")

			column(name: "possible_values_idx", type: "integer")
		}
	}
}
