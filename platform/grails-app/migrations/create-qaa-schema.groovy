databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1457701567336-1") {
		createTable(tableName: "answer_asset") {
			column(name: "answer_attachments_id", type: "varchar(255)")
			column(name: "asset_id", type: "varchar(255)")
			column(name: "attachments_idx", type: "integer")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-2") {
		createTable(tableName: "answer_rating_users") {
			column(name: "answer_id", type: "varchar(255)")
			column(name: "rating_users_long", type: "bigint")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-3") {
		createTable(tableName: "comment") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "commentPK")
			}
			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}
			column(name: "creator_id", type: "bigint") {
				constraints(nullable: "false")
			}
			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}
			column(name: "deleted", type: "boolean") {
				constraints(nullable: "false")
			}
			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}
			column(name: "reference_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}
			column(name: "text", type: "longvarchar") {
				constraints(nullable: "false")
			}
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-4") {
		createTable(tableName: "commentable") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "commentablePK")
			}
			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}
			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}
			column(name: "creator_id", type: "bigint")
			column(name: "date_created", type: "timestamp")
			column(name: "deleted", type: "boolean")
			column(name: "last_updated", type: "timestamp")
			column(name: "question_id", type: "varchar(255)")
			column(name: "text", type: "longvarchar")
			column(name: "accepted_answer_id", type: "varchar(255)")
			column(name: "reference_id", type: "varchar(255)")
			column(name: "title", type: "longvarchar")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-5") {
		createTable(tableName: "question_asset") {
			column(name: "question_attachments_id", type: "varchar(255)")
			column(name: "asset_id", type: "varchar(255)")
			column(name: "attachments_idx", type: "integer")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-6") {
		createTable(tableName: "question_metadata") {
			column(name: "metadata", type: "varchar(255)")
			column(name: "metadata_idx", type: "varchar(255)")
			column(name: "metadata_elt", type: "longvarchar") {
				constraints(nullable: "false")
			}
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-7") {
		createTable(tableName: "question_rating_users") {
			column(name: "question_id", type: "varchar(255)")
			column(name: "rating_users_long", type: "bigint")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-9") {
		createTable(tableName: "question_taxonomy_term") {
			column(name: "question_terms_id", type: "varchar(255)")

			column(name: "taxonomy_term_id", type: "varchar(255)")
		}
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-33") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "comment", constraintName: "FK_kcxienwlty3t2mxujrvnve4pv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-31") {
		addForeignKeyConstraint(baseColumnNames: "answer_id", baseTableName: "answer_rating_users", constraintName: "FK_rcohco69kbckiae3mrmox4ju8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-34") {
		addForeignKeyConstraint(baseColumnNames: "reference_id", baseTableName: "comment", constraintName: "FK_3tfkdcmf6rg6hcyiu8t05er7x", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-35") {
		addForeignKeyConstraint(baseColumnNames: "accepted_answer_id", baseTableName: "commentable", constraintName: "FK_tmt5qvrnwkj371pm7extgp0df", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-36") {
		addForeignKeyConstraint(baseColumnNames: "creator_id", baseTableName: "commentable", constraintName: "FK_ny93qmvmvqwboigesbrx2mfrd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-37") {
		addForeignKeyConstraint(baseColumnNames: "question_id", baseTableName: "commentable", constraintName: "FK_72ajo5wpa0035y3e9x16035hl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-38") {
		addForeignKeyConstraint(baseColumnNames: "reference_id", baseTableName: "commentable", constraintName: "FK_k4gvxgafesi1njniq6oalybyp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "question_reference", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-40") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "question_asset", constraintName: "FK_o5jurrfahkvuih0om0xq3mi88", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-41") {
		addForeignKeyConstraint(baseColumnNames: "question_id", baseTableName: "question_rating_users", constraintName: "FK_6tu1ywsyrbaex087rbl4nt98r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-45") {
		addForeignKeyConstraint(baseColumnNames: "question_terms_id", baseTableName: "question_taxonomy_term", constraintName: "FK_s91oqoboiw5ke7v0s2jaosvgf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "commentable", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-46") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "question_taxonomy_term", constraintName: "FK_6i5agnnmby0nrdie5bhtyd7gd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_term", referencesUniqueColumn: "false")
	}
	changeSet(author: "tittel (generated)", id: "1457701567336-30") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "answer_asset", constraintName: "FK_tli75ca8x8q0pxh3q5t143jjy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}
}
