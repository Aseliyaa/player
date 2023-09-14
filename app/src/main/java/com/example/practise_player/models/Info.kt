package com.example.practise_player.models


class Info {
    var id: String? = null
    var textBtn1: String? = null
    var textBtn2: String? = null
    var endTime: Long = 0
    var startTime: Long = 0
    var button1IntervalStart: Long = 0
    var button1IntervalEnd: Long = 0
    var button2IntervalStart: Long = 0
    var button2IntervalEnd: Long = 0

    constructor()

    constructor(
        id: String?,
        textBtn1: String?,
        textBtn2: String?,
        endTime: Long,
        startTime: Long,
        button1IntervalStart: Long,
        button1IntervalEnd: Long,
        button2IntervalStart: Long,
        button2IntervalEnd: Long
    ) {
        this.id = id
        this.textBtn1 = textBtn1
        this.textBtn2 = textBtn2
        this.endTime = endTime
        this.startTime = startTime
        this.button1IntervalStart = button1IntervalStart
        this.button1IntervalEnd = button1IntervalEnd
        this.button2IntervalStart = button2IntervalStart
        this.button2IntervalEnd = button2IntervalEnd
    }
}
