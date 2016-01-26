databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1453714500813-1") {
		addColumn(tableName: "task_step") {
			column(name: "steps_idx", type: "integer")
		}
		sql(''' update task_step set steps_idx = select STEPS_IDX from TASK_TASK_STEP where TASK_STEP_ID=id ''' )
	}

	changeSet(author: "tittel (generated)", id: "1453714500813-2") {
		addColumn(tableName: "task_step") {
			column(name: "task_id", type: "varchar(255)")
		}
		sql(''' update task_step set task_id = select TASK_STEPS_ID from TASK_TASK_STEP where TASK_STEP_ID=id ''' )
		addNotNullConstraint(columnDataType: "varchar(255)", columnName: "task_id", tableName: "task_step")
	}

	changeSet(author: "tittel (generated)", id: "1453714500813-16") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "task_step", constraintName: "FK_jbs7cywwesnks7d2c8nu2typy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453714500813-15") {
		dropForeignKeyConstraint(baseTableName: "TASK_TASK_STEP", baseTableSchemaName: "PUBLIC", constraintName: "FK_HFIYKE51Q6OLPEH6J5HL9BUY0")
	}

	changeSet(author: "tittel (generated)", id: "1453714500813-17") {
		dropTable(tableName: "TASK_TASK_STEP")
	}
}
