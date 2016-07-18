databaseChangeLog = {
	changeSet(author: "tittel (generated)", id: "1455263084992-7") {
		addColumn(tableName: "asset") {
			column(name: "type_label", type: "varchar(255)")
		}
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-6") {
		addColumn(tableName: "asset") {
			column(name: "date_created", type: "timestamp")
		}
		sql("update asset set date_created=last_updated, type_label=sub_type")
		addNotNullConstraint(columnDataType:"timestamp", columnName:"date_created", tableName:"asset")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-1") {
		createTable(tableName: "asset_props") {
			column(name: "props", type: "varchar(255)")
			column(name: "props_idx", type: "varchar(255)")
			column(name: "props_elt", type: "longvarchar") {
				constraints(nullable: "false")
			}
		}
		sql("insert into asset_props (props, props_idx, props_elt) select a.id, '_anchor', a.anchor from asset a where a.anchor is not null")
		sql("insert into asset_props (props, props_idx, props_elt) select a.id, '_description', a.description from asset a where a.description is not null")
		sql("insert into asset_props (props, props_idx, props_elt) select a.id, '_filename', a.filename from asset a where a.filename is not null")
		sql("insert into asset_props (props, props_idx, props_elt) select a.id, '_externalUrl', a.external_url from asset a where a.external_url is not null")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-2") {
		createTable(tableName: "asset_taxonomy_term") {
			column(name: "asset_categories_id", type: "varchar(255)")
			column(name: "taxonomy_term_id", type: "varchar(255)")
		}
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-23") {
		dropColumn(columnName: "ANCHOR", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-24") {
		dropColumn(columnName: "DESCRIPTION", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-25") {
		dropColumn(columnName: "EXTERNAL_URL", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-26") {
		dropColumn(columnName: "FILENAME", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-27") {
		dropColumn(columnName: "SUB_TYPE", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-3") {
		createTable(tableName: "taxonomy") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "taxonomyPK")
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

			column(name: "type", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-4") {
		createTable(tableName: "taxonomy_taxonomy_term") {
			column(name: "taxonomy_terms_id", type: "varchar(255)")
			column(name: "taxonomy_term_id", type: "varchar(255)")
			column(name: "terms_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-5") {
		createTable(tableName: "taxonomy_term") {
			column(name: "id", type: "varchar(255)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "taxonomy_termPK")
			}
			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}
			column(name: "esa_vector_data", type: "binary(52428800)")
			column(name: "is_primary_domain", type: "boolean") {
				constraints(nullable: "false")
			}
			column(name: "label", type: "longvarchar") {
				constraints(nullable: "false")
			}
			column(name: "parent_id", type: "varchar(255)")
			column(name: "children_idx", type: "integer")
		}
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-19") {
		addForeignKeyConstraint(baseColumnNames: "asset_categories_id", baseTableName: "asset_taxonomy_term", constraintName: "FK_f6m2im79sb6f9vhaj6anbl53f", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-20") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "asset_taxonomy_term", constraintName: "FK_f61rsm1li38ku6kowmteja2ga", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_term", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-21") {
		addForeignKeyConstraint(baseColumnNames: "taxonomy_term_id", baseTableName: "taxonomy_taxonomy_term", constraintName: "FK_tny3pklkiwwbptvdkq39i8h82", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_term", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1455263084992-22") {
		addForeignKeyConstraint(baseColumnNames: "parent_id", baseTableName: "taxonomy_term", constraintName: "FK_pm2d947838nel445pc212wc6w", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "taxonomy_term", referencesUniqueColumn: "false")
	}

	changeSet(author: "tittel (generated)", id: "1455523661444-1") {
		createTable(tableName: "asset_content") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "asset_contentPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "asset_id", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "data", type: "binary(104857600)") {
				constraints(nullable: "false")
			}
		}
		sql("insert into asset_content (version, asset_id, data) select 0, a.id, a.content from asset a where a.content is not null")
	}

	changeSet(author: "tittel (generated)", id: "1455523661444-25") {
		createIndex(indexName: "asset_id_uniq_1455523661282", tableName: "asset_content", unique: "true") {
			column(name: "asset_id")
		}
	}

	changeSet(author: "tittel (generated)", id: "1455523661444-27") {
		dropColumn(columnName: "CONTENT", tableName: "ASSET")
	}

	changeSet(author: "tittel (generated)", id: "1455523661444-20") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "asset_content", constraintName: "FK_4yshq8i94w9j68huut978pfla", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
	}

}
