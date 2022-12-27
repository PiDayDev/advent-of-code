package common


class ArithmeticCalculator(val expression: String) {
    private val simpleExpression = """\([-+*/ 0-9]+\)""".toRegex()
    private val spaces = """\s+""".toRegex()

    fun calculate(): Long =
        solve(expression, ::solveBasicWithPartialPrecedence)

    private fun solve(string: String, simplify: String.() -> Long): Long =
        if (string.contains("(")) {
            solve(string.replace(simpleExpression) {
                "${it.value.removeSurrounding("(", ")").simplify()}"
            }, simplify)
        } else {
            string.simplify()
        }

    private fun MutableList<String>.splice(around: Int, op: (Long, Long) -> Long) {
        val b = removeAt(around + 1)
        removeAt(around)
        val a = removeAt(around - 1)
        add(around - 1, op(a.toLong(), b.toLong()).toString())
    }

    private fun solveBasicWithPartialPrecedence(s: String): Long {
        val tokens = s.split(spaces).toMutableList()
        var times = tokens.indexOf("*")
        while (times >= 0) {
            tokens.splice(times) { a, b -> a * b }
            times = tokens.indexOf("*")
        }
        var div = tokens.indexOf("/")
        while (div >= 0) {
            tokens.splice(div) { a, b -> a / b }
            div = tokens.indexOf("/")
        }
        var plus = tokens.indexOf("+")
        while (plus >= 0) {
            tokens.splice(plus) { a, b -> a + b }
            plus = tokens.indexOf("+")
        }
        var minus = tokens.indexOf("-")
        while (minus >= 0) {
            tokens.splice(minus) { a, b -> a - b }
            minus = tokens.indexOf("-")
        }
        check(tokens.size == 1)
        return tokens.first().toLong()
    }


}
