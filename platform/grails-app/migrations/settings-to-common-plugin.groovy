databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1473152404964-4") {
		createTable(tableName: "setting") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "settingPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "key", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "multiline", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "prefix", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "required", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "longvarchar")

			column(defaultValue: "1.0", name: "weight", type: "float") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1473152404964-18") {
		createIndex(indexName: "key_uniq_1473152404730", tableName: "setting", unique: "true") {
			column(name: "key")
		}
	}

	changeSet(author: "tittel (generated)", id: "1473152404964-19") {
		createIndex(indexName: "weight_uniq_1473152404731", tableName: "setting", unique: "true") {
			column(name: "weight")
		}
	}

	changeSet(author: "tittel (generated)", id: "1473152404964-20") {
		dropTable(tableName: "SETTINGS")
	}
}
