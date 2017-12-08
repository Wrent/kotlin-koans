package iii_conventions


data class MyDate (val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate): Int {
        val compareYear = this.year.compareTo(other.year)
        if (compareYear != 0) {
            return compareYear;
        }
        val compareMonth = this.month.compareTo(other.month)
        if (compareMonth != 0) {
            return compareMonth;
        }
        return this.dayOfMonth.compareTo(other.dayOfMonth);
    }

    fun nextDay() : MyDate {
        var day = dayOfMonth
        var month = month
        var year = year
        if (isInMiddleOfMonth(day, month)) {
            day = dayOfMonth + 1
        } else if (isLastFebruary(day, month)) {
            if (isLeapYear(year)) {
                day = 29
                month = 1
            } else {
                day = 1
                month = 2
            }
        } else if (isBetween28and30(day)) {
            if (isShortMonth(month)) {
                day = 1
                month++
            } else {
                day++
            }
        } else {
            if (isLastMonth(month)) {
                day = 1
                month = 0
                year++
            } else {
                day = 1
                month++
            }
        }

        return MyDate(year, month, day)
    }

    private fun isLastMonth(month: Int) = month == 11

    private fun isShortMonth(month: Int) = month == 1 || month == 3 || month == 5 || month == 8 || month == 9

    private fun isBetween28and30(day: Int) = day in 28..30

    private fun isLeapYear(year: Int) = year % 4 == 0

    private fun isLastFebruary(day: Int, month: Int) = day == 28 && month == 1

    protected fun isInMiddleOfMonth(day: Int, month: Int) = day < 28 || (day == 29 && month != 1)
}

operator fun MyDate.rangeTo(other: MyDate): DateRange {
    return DateRange(this, other);
}

operator fun MyDate.plus(interval: TimeInterval): MyDate {
    when (interval) {
        TimeInterval.YEAR ->
                return MyDate(this.year + 1, this.month, this.dayOfMonth)
        TimeInterval.WEEK -> {
            var i = 0
            var date : MyDate = this
            while (i < 7) {
                date = date.nextDay()
                i++
            }
            return date
        }
        TimeInterval.DAY ->
                return this.nextDay();
    }
}

operator fun MyDate.plus(interval: RepeatedTimeInterval) : MyDate {
    var i = 0
    var date : MyDate = this
    while (i < interval.n) {
        date += interval.ti
        i++
    }
    return date
}

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR
}

operator fun TimeInterval.times(n: Int) : RepeatedTimeInterval {
    return RepeatedTimeInterval(this, n)
}

class DateRange(val start: MyDate, val endInclusive: MyDate) : Iterable<MyDate> {
    operator fun contains(date: MyDate): Boolean {
        return date >= start && date <= endInclusive
    }

    override fun iterator(): Iterator<MyDate> {
        return DateRangeIterator(this)
    }
}

class DateRangeIterator(range: DateRange) : Iterator<MyDate> {
    private var start : MyDate
    private val end: MyDate

    init {
        start = range.start
        end = range.endInclusive
    }

    override fun next(): MyDate {
        val value = start
        start = start.nextDay()
        return value
    }

    override fun hasNext(): Boolean {
        return start <= end
    }

}

class RepeatedTimeInterval(val ti: TimeInterval, val n: Int)
