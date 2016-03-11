databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1457701567336-8") {
		createTable(tableName: "question_reference") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "question_refePK")
			}
			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}
			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}
			column(name: "assignee_id", type: "bigint")
			column(name: "creator_id", type: "bigint")
			column(name: "date_created", type: "timestamp")
			column(name: "deleted", type: "boolean")
			column(name: "description", type: "longvarchar")
			column(name: "done", type: "boolean")
			column(name: "due", type: "timestamp")
			column(name: "is_template", type: "boolean")
			column(name: "last_updated", type: "timestamp")
			column(name: "name", type: "longvarchar")
			column(name: "template_id", type: "varchar(255)")
		}
		sql("insert into question_reference (id,version,class,assignee_id,creator_id,date_created,deleted,description,done,due,is_template,last_updated,name,template_id) select t.id,t.version,'kola.Task',t.assignee_id,t.creator_id,t.date_created,t.deleted,t.description,t.done,t.due,t.is_template,t.last_updated,t.name,t.template_id from task t")
		sql("insert into question_reference (id,version,class,creator_id,description,name) select t.id,t.version,'kola.TaskStep',t.creator_id,t.description,t.name from task_step t")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-10") {
		createTable(tableName: "task_task_step") {
			column(name: "task_steps_id", type: "varchar(255)")
			column(name: "task_step_id", type: "varchar(255)")
			column(name: "steps_idx", type: "integer")
		}
		sql("insert into task_task_step (task_steps_id, task_step_id, steps_idx) select s.task_id, s.id, s.steps_idx from task_step s")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-11") {
		addColumn(tableName: "task_documentation") {
			column(name: "reference_id", type: "varchar(255)")
		}
		sql("update task_documentation set reference_id=task_id where task_id is not null")
		sql("update task_documentation set reference_id=step_id where step_id is not null")
		addNotNullConstraint(columnDataType:"varchar(255)", columnName:"reference_id", tableName:"task_documentation")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-22") {
		dropForeignKeyConstraint(baseTableName: "REFLECTION_ANSWER", baseTableSchemaName: "PUBLIC", constraintName: "FK_SMIXIKT3CMCWWNGMF1RPYXQ7K")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-23") {
		dropForeignKeyConstraint(baseTableName: "TASK", baseTableSchemaName: "PUBLIC", constraintName: "FK_RB8JSUWOIMKIAS723H7DTDAA3")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-24") {
		dropForeignKeyConstraint(baseTableName: "TASK", baseTableSchemaName: "PUBLIC", constraintName: "FK_CO6UCUP90YMW724XGSEOHHM80")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-25") {
		dropForeignKeyConstraint(baseTableName: "TASK", baseTableSchemaName: "PUBLIC", constraintName: "FK_LJ0HVDPHKO87CN5E4UOXMJY4H")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-26") {
		dropForeignKeyConstraint(baseTableName: "TASK_DOCUMENTATION", baseTableSchemaName: "PUBLIC", constraintName: "FK_NWOJJGPSV8FHJBMAVMWRHS5AR")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-27") {
		dropForeignKeyConstraint(baseTableName: "TASK_DOCUMENTATION", baseTableSchemaName: "PUBLIC", constraintName: "FK_250MNSC8S2H5K1I2OHVY1Y319")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-28") {
		dropForeignKeyConstraint(baseTableName: "TASK_STEP", baseTableSchemaName: "PUBLIC", constraintName: "FK_7DOHF1EF1W61WMBXXPMGNUJ91")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-29") {
		dropForeignKeyConstraint(baseTableName: "TASK_STEP", baseTableSchemaName: "PUBLIC", constraintName: "FK_JBS7CYWWESNKS7D2C8NU2TYPY")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-50") {
		createIndex(indexName: "asset_id_uniq_1457701567138", tableName: "asset_content", unique: "true") {
			column(name: "asset_id")
		}
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-52") {
		dropColumn(columnName: "STEP_ID", tableName: "TASK_DOCUMENTATION")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-53") {
		dropColumn(columnName: "TASK_ID", tableName: "TASK_DOCUMENTATION")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-54") {
		dropTable(tableName: "TASK")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-55") {
		dropTable(tableName: "TASK_STEP")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-39") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "push_token", constraintName: "FK_c86q6937piil4361t2u6icwdt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-42") {
		addForeignKeyConstraint(baseColumnNames: "assignee_id", baseTableName: "question_reference", constraintName: "FK_4x459y19k79eo83ygx4l7abee", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-43") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "question_reference", constraintName: "FK_l606xi71qtkjglerku3ubapr0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-44") {
		addForeignKeyConstraint(baseColumnNames: "template_id", baseTableName: "question_reference", constraintName: "FK_9p078ja5a2qo4m4oa0vnis9rv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-47") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "reflection_answer", constraintName: "FK_smixikt3cmcwwngmf1rpyxq7k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-48") {
		addForeignKeyConstraint(baseColumnNames: "reference_id", baseTableName: "task_documentation", constraintName: "FK_jui3bk5srkola8w8kswoxuv7q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1457701567336-49") {
		addForeignKeyConstraint(baseColumnNames: "task_step_id", baseTableName: "task_task_step", constraintName: "FK_hfiyke51q6olpeh6j5hl9buy0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}
}
