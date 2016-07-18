import kola.TaskDocumentation

databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1468833179225-15") {
		sql("INSERT INTO COMMENTABLE (ID, VERSION, CLASS, CREATOR_ID, DELETED, LAST_UPDATED, REFERENCE_ID, TEXT) SELECT ID, VERSION, '${TaskDocumentation.class.name}', CREATOR_ID, DELETED, LAST_UPDATED, REFERENCE_ID, TEXT FROM TASK_DOCUMENTATION")
	}

	changeSet(author: "tittel (generated)", id: "1468833179225-16") {
		dropForeignKeyConstraint(baseTableName: "TASK_DOCUMENTATION", baseTableSchemaName: "PUBLIC", constraintName: "FK_NQP3SPVAHX28S3LA9TBH12OM3")
	}

	changeSet(author: "tittel (generated)", id: "1468833179225-17") {
		dropForeignKeyConstraint(baseTableName: "TASK_DOCUMENTATION", baseTableSchemaName: "PUBLIC", constraintName: "FK_JUI3BK5SRKOLA8W8KSWOXUV7Q")
	}

	changeSet(author: "tittel (generated)", id: "1468833179225-18") {
		dropTable(tableName: "TASK_DOCUMENTATION")
	}
}
