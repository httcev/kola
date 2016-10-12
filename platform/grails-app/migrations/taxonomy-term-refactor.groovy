databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1474458226310-4") {
		createTable(tableName: "taxonomy_node") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "taxonomy_nodePK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "label", type: "longvarchar") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "esa_vector_data", type: "binary(52428800)")

			column(name: "parent_id", type: "varchar(255)")

			column(name: "taxonomy_id", type: "varchar(255)")

			column(name: "type", type: "varchar(255)")

			column(name: "children_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-17") {
		dropForeignKeyConstraint(baseTableName: "ASSET_TAXONOMY_TERM", baseTableSchemaName: "PUBLIC", constraintName: "FK_F61RSM1LI38KU6KOWMTEJA2GA")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-18") {
		dropForeignKeyConstraint(baseTableName: "QUESTION_REFERENCE", baseTableSchemaName: "PUBLIC", constraintName: "FK_PBXTWBRKLQPBFP37V2H9ETHJF")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-19") {
		dropForeignKeyConstraint(baseTableName: "QUESTION_TAXONOMY_TERM", baseTableSchemaName: "PUBLIC", constraintName: "FK_6I5AGNNMBY0NRDIE5BHTYD7GD")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-20") {
		dropForeignKeyConstraint(baseTableName: "TAXONOMY_TERM", baseTableSchemaName: "PUBLIC", constraintName: "FK_PM2D947838NEL445PC212WC6W")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-21") {
		dropForeignKeyConstraint(baseTableName: "TAXONOMY_TERM", baseTableSchemaName: "PUBLIC", constraintName: "FK_KJ3OTKO4OHDX1Y9S77O1SFKD1")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-27") {
		dropIndex(indexName: "LABEL_UNIQ_1470913842362", tableName: "TAXONOMY")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-28") {
		dropTable(tableName: "TAXONOMY")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-29") {
		dropTable(tableName: "TAXONOMY_TERM")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-22") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "asset_taxonomy_term", constraintName: "FK_f61rsm1li38ku6kowmteja2ga", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-23") {
		addForeignKeyConstraint(baseColumnNames: "type_id", baseTableName: "question_reference", constraintName: "FK_pbxtwbrklqpbfp37v2h9ethjf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-24") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "question_taxonomy_term", constraintName: "FK_6i5agnnmby0nrdie5bhtyd7gd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-25") {
		addForeignKeyConstraint(baseColumnNames: "parent_id", baseTableName: "taxonomy_node", constraintName: "FK_plaoqnknqgfwlav07mlxbg2h0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1474458226310-26") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_id", baseTableName: "taxonomy_node", constraintName: "FK_ddueml8i31xhiu52b0eecodif", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1476264310037-4") {
		createTable(tableName: "profile_taxonomy_term") {
			column(name: "profile_organisations_id", type: "bigint")

			column(name: "taxonomy_term_id", type: "varchar(255)")

			column(name: "organisations_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1476264310037-19") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "profile_taxonomy_term", constraintName: "FK_trwl8b8uh1sn5uq5yrxeyhy4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_node", referencesUniqueColumn: "false")
	}
}
