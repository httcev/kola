databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1470913842589-1") {
		addColumn(tableName: "question_reference") {
			column(name: "type_id", type: "varchar(255)")
		}
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-21") {
		addForeignKeyConstraint(baseColumnNames: "type_id", baseTableName: "question_reference", constraintName: "FK_pbxtwbrklqpbfp37v2h9ethjf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_term", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-3") {
		addColumn(tableName: "taxonomy_term") {
			column(name: "taxonomy_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-4") {
		addColumn(tableName: "taxonomy_term") {
			column(name: "terms_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-20") {
		dropForeignKeyConstraint(baseTableName: "TAXONOMY_TAXONOMY_TERM", baseTableSchemaName: "PUBLIC", constraintName: "FK_TNY3PKLKIWWBPTVDKQ39I8H82")
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-23") {
		createIndex(indexName: "label_uniq_1470913842362", tableName: "taxonomy", unique: "true") {
			column(name: "label")
		}
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-24") {
		dropTable(tableName: "TAXONOMY_TAXONOMY_TERM")
	}

	changeSet(author: "tittel (generated)", id: "1470913842589-22") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_id", baseTableName: "taxonomy_term", constraintName: "FK_kj3otko4ohdx1y9s77o1sfkd1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy", referencesUniqueColumn: "false")
	}
}
