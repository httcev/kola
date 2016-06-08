databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1465391083588-1") {
		addColumn(tableName: "question_reference") {
			column(name: "steps_idx", type: "integer")
		}
		sql("update question_reference r set r.steps_idx=(select t.steps_idx from task_task_step t where t.task_step_id=r.id)")
	}

	changeSet(author: "tittel (generated)", id: "1465391083588-2") {
		addColumn(tableName: "question_reference") {
			column(name: "task_id", type: "varchar(255)")
		}
		sql("update question_reference r set r.task_id=(select t.task_steps_id from task_task_step t where t.task_step_id=r.id)")
	}

	changeSet(author: "tittel (generated)", id: "1465391083588-19") {
		dropForeignKeyConstraint(baseTableName: "TASK_TASK_STEP", baseTableSchemaName: "PUBLIC", constraintName: "FK_HFIYKE51Q6OLPEH6J5HL9BUY0")
	}

	changeSet(author: "tittel (generated)", id: "1465391083588-21") {
		dropTable(tableName: "TASK_TASK_STEP")
	}

	changeSet(author: "tittel (generated)", id: "1465391083588-20") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "question_reference", constraintName: "FK_ct0o6fmw4vu4bkc8yj776l7vt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}
}
