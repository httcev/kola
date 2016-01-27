databaseChangeLog = {

	changeSet(author: "tittel (generated)", id: "1453819262522-1") {
		createTable(tableName: "asset") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "assetPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "anchor", type: "varchar(255)")

			column(name: "content", type: "binary(104857600)")

			column(name: "creator_id", type: "bigint")

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "longvarchar")

			column(name: "external_url", type: "varchar(255)")

			column(name: "filename", type: "varchar(255)")

			column(name: "index_text", type: "longvarchar")

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "mime_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sub_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-2") {
		createTable(tableName: "profile") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "profilePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "company", type: "varchar(255)")

			column(name: "first_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp")

			column(name: "mobile", type: "varchar(255)")

			column(name: "phone", type: "varchar(255)")

			column(name: "photo", type: "binary(2097152)")

			column(name: "user_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-3") {
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

	changeSet(author: "tittel (generated)", id: "1453819262522-4") {
		createTable(tableName: "reflection_answer") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "reflection_anPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "creator_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "question_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "task_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "text", type: "longvarchar") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-5") {
		createTable(tableName: "reflection_question") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "reflection_quPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(defaultValue: "0", name: "auto_link", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "longvarchar") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-6") {
		createTable(tableName: "registration_code") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "registration_PK")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-7") {
		createTable(tableName: "role") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-8") {
		createTable(tableName: "settings") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "settingsPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "single_entry", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "terms_of_use", type: "longvarchar")

			column(name: "welcome_body", type: "longvarchar") {
				constraints(nullable: "false")
			}

			column(name: "welcome_header", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-9") {
		createTable(tableName: "task") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "taskPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "assignee_id", type: "bigint")

			column(name: "creator_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "longvarchar")

			column(name: "done", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "due", type: "timestamp")

			column(name: "is_template", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "longvarchar") {
				constraints(nullable: "false")
			}

			column(name: "template_id", type: "varchar(255)")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-10") {
		createTable(tableName: "task_asset") {
			column(name: "task_attachments_id", type: "varchar(255)")

			column(name: "asset_id", type: "varchar(255)")

			column(name: "attachments_idx", type: "integer")

			column(name: "task_resources_id", type: "varchar(255)")

			column(name: "resources_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-11") {
		createTable(tableName: "task_documentation") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "task_documentPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "creator_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "step_id", type: "varchar(255)")

			column(name: "task_id", type: "varchar(255)")

			column(name: "text", type: "longvarchar")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-12") {
		createTable(tableName: "task_documentation_asset") {
			column(name: "task_documentation_attachments_id", type: "varchar(255)")

			column(name: "asset_id", type: "varchar(255)")

			column(name: "attachments_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-13") {
		createTable(tableName: "task_reflection_question") {
			column(name: "task_reflection_questions_id", type: "varchar(255)")

			column(name: "reflection_question_id", type: "bigint")

			column(name: "reflection_questions_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-14") {
		createTable(tableName: "task_step") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "task_stepPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "creator_id", type: "bigint")

			column(name: "description", type: "longvarchar")

			column(name: "name", type: "longvarchar") {
				constraints(nullable: "false")
			}

			column(name: "task_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "steps_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-15") {
		createTable(tableName: "task_step_asset") {
			column(name: "task_step_attachments_id", type: "varchar(255)")

			column(name: "asset_id", type: "varchar(255)")

			column(name: "attachments_idx", type: "integer")

			column(name: "task_step_resources_id", type: "varchar(255)")

			column(name: "resources_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-16") {
		createTable(tableName: "user") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "userPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "passwd", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "terms_of_use_accepted", type: "boolean") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-17") {
		createTable(tableName: "user_role") {
			column(name: "user_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "role_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-18") {
		addPrimaryKey(columnNames: "user_id, role_id", constraintName: "user_rolePK", tableName: "user_role")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-38") {
		createIndex(indexName: "unique_first_name", tableName: "profile", unique: "true") {
			column(name: "last_name")

			column(name: "first_name")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-39") {
		createIndex(indexName: "user_id_uniq_1453819262422", tableName: "profile", unique: "true") {
			column(name: "user_id")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-40") {
		createIndex(indexName: "authority_uniq_1453819262429", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-41") {
		createIndex(indexName: "username_uniq_1453819262439", tableName: "user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-19") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "asset", constraintName: "FK_a1v1wr70nkwjsus1id2vtscm8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-20") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "profile", constraintName: "FK_c1dkiawnlj6uoe6fnlwd6j83j", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-21") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "reflection_answer", constraintName: "FK_r0b9suvvwk8a3okw5c7b2udvw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-22") {
		addForeignKeyConstraint(baseColumnNames: "question_id", baseTableName: "reflection_answer", constraintName: "FK_45o12tgot573psxv8is1776w3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "reflection_question", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-23") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "reflection_answer", constraintName: "FK_smixikt3cmcwwngmf1rpyxq7k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-24") {
		addForeignKeyConstraint(baseColumnNames: "assignee_id", baseTableName: "task", constraintName: "FK_rb8jsuwoimkias723h7dtdaa3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-25") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "task", constraintName: "FK_co6ucup90ymw724xgseohhm80", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-26") {
		addForeignKeyConstraint(baseColumnNames: "template_id", baseTableName: "task", constraintName: "FK_lj0hvdphko87cn5e4uoxmjy4h", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-27") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "task_asset", constraintName: "FK_9sxixf4pasw8a1ysafsmergq1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-28") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "task_documentation", constraintName: "FK_nqp3spvahx28s3la9tbh12om3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-29") {
		addForeignKeyConstraint(baseColumnNames: "step_id", baseTableName: "task_documentation", constraintName: "FK_nwojjgpsv8fhjbmavmwrhs5ar", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task_step", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-30") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "task_documentation", constraintName: "FK_250mnsc8s2h5k1i2ohvy1y319", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-31") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "task_documentation_asset", constraintName: "FK_lbmwilb1viq7cb4huhaa8mnms", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-32") {
		addForeignKeyConstraint(baseColumnNames: "reflection_question_id", baseTableName: "task_reflection_question", constraintName: "FK_p0geqr4n49262w8catjjn7why", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "reflection_question", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-33") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "task_step", constraintName: "FK_7dohf1ef1w61wmbxxpmgnuj91", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-34") {
		addForeignKeyConstraint(baseColumnNames: "task_id", baseTableName: "task_step", constraintName: "FK_jbs7cywwesnks7d2c8nu2typy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "task", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-35") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "task_step_asset", constraintName: "FK_2211vn9ajw3i7tyii4ois7sbe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-36") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FK_it77eq964jhfqtu54081ebtio", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1453819262522-37") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK_apcc8lxk2xnug8377fatvbn04", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}
}
