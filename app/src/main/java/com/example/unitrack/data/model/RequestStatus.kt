package com.example.unitrack.data.model

enum class RequestStatus(
    val dbValue: String,
    val label: String
) {
    SUBMITTED("SUBMITTED", "Submetido"),
    IN_ANALYSIS("IN_ANALYSIS", "Em análise"),
    COMPLETED("COMPLETED", "Concluído"),
    REJECTED("REJECTED", "Rejeitado"),
    CANCELLED("CANCELLED", "Cancelado");

    companion object {
        fun getLabelFromDbValue(value: String): String {
            return values().find { it.dbValue == value }?.label ?: value
        }
    }
}