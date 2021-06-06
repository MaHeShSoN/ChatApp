package com.exampley.charapplication.models

class chatMassage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timeScpae: Long
) {
    constructor() : this("", "", "", "", -1)
}