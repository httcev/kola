databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1453202200601-1") {
		createTable(tableName: "push_token") {
			column(autoIncrement: "true", name: "user_id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "push_tokenPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453202200601-21") {
		dropTable(tableName: "COUCH_SYNCHRONIZER_SETTINGS")
	}
}
