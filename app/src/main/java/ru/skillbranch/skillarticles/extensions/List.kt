package ru.skillbranch.skillarticles.extensions


// Реализуй fun List для группировки результата поиска по интервалам указанным в коллекции bounds.
// Количество выходных элементов должно быть рано количеству bounds.
// Пример:
// searchResult = [(2,5), (8,20), (22,30), (45,50), (70,100)]
// bounds = [(0,10), (10,30), (30,50), (50,60), (60,100)]
// result = [[(2, 5), (8, 10)], [(10, 20), (22, 30)], [(45, 50)], [], [(70, 100)]]
fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> {
    if (bounds.isEmpty()) return listOf()

    val res = List<MutableList<Pair<Int, Int>>>(bounds.size) { mutableListOf() }
    if (this.isEmpty()) return res

    var start: Int
    var end: Int

    this.fold(res) { acc, el ->
        start = bounds.indexOfFirst { bound -> el.first <= bound.second }
        end = bounds.indexOfFirst { bound -> el.second <= bound.second }
        if (start == end) {
            acc[start] += el
        } else {
            acc[start] += el.first to bounds[start].second
            acc[end] += bounds[end].first to el.second
            for (i in start..end) {
                if (i in (start + 1) until end) acc[i] += bounds[i]
            }
        }
        acc
    }
    return res
}