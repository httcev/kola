databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1453202200601-2") {
		addColumn(tableName: "profile") {
			column(name: "first_name", type: "varchar(255)")
		}
		sql(''' update profile set first_name = SUBSTRING(display_name, 0, LOCATE(' ', display_name) - 1) ''' )
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "first_name", tableName: "profile", defaultNullValue: "n/a")
	}

	changeSet(author: "tittel (generated)", id: "1453202200601-3") {
		addColumn(tableName: "profile") {
			column(name: "last_name", type: "varchar(255)")
		}
		sql(''' update profile set last_name = SUBSTRING(display_name, LOCATE(' ', display_name) + 1) ''' )
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "last_name", tableName: "profile", defaultNullValue: "n/a")
	}

	changeSet(author: "tittel (generated)", id: "1453202200601-16") {
		modifyDataType(columnName: "TERMS_OF_USE_ACCEPTED", newDataType: "boolean", tableName: "USER")
	}

	changeSet(author: "tittel (generated)", id: "1453202200601-17") {
		addNotNullConstraint(columnDataType: "boolean", columnName: "TERMS_OF_USE_ACCEPTED", tableName: "USER", defaultNullValue: "false")
	}
/*
	changeSet(author: "tittel (generated)", id: "1453202200601-18") {
		dropIndex(indexName: "UK_9RSVSJ3YHJCD3IXRV832R907Y_INDEX_8", tableName: "PROFILE")
	}
*/
	changeSet(author: "tittel (generated)", id: "1453202200601-19") {
		createIndex(indexName: "unique_first_name", tableName: "profile", unique: "true") {
			column(name: "last_name")
			column(name: "first_name")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453202200601-20") {
		dropColumn(columnName: "DISPLAY_NAME", tableName: "PROFILE")
	}
}
