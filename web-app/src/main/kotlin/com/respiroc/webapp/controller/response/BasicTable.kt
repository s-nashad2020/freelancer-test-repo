package com.respiroc.webapp.controller.response

data class Table(
    val columns: List<Column>,
    val rows: List<Map<String, Any>>,
    val idField: String,
    val actions: List<TableAction>
)

data class Column(
    val field: String,
    val label: String
)

data class TableAction(
    val label: String,
    val method: String,
    val url: String,    // sample: /customer/new/{id}
    val confirmMessage: String? = null
)