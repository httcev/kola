databaseChangeLog = {
	changeSet(author: "tittel", id: "1") {
        renameColumn(tableName: "user", oldColumnName: "\"password\"", newColumnName: "passwd")
    }
}