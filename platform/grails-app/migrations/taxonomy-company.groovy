databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1574849546512-4") {
		addColumn(tableName: "profile") {
			column(name: "company_id", type: "varchar(255)")
		}
	}

	changeSet(author: "tittel (generated)", id: "1574849546512-23") {
		dropColumn(columnName: "COMPANY", tableName: "PROFILE")
	}

	changeSet(author: "tittel (generated)", id: "1574849546512-22") {
		addForeignKeyConstraint(baseColumnNames: "company_id", baseTableName: "profile", constraintName: "FK_m9d2ovhpsfl6akpp48owp7gxc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}
}
