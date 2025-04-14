package com.tanucode.formulario.formulas

data class Formula(
    val name: String,
    val variableNames: List<String>,
    val calculate: (Map<String, Double>) -> List<Double>
) {
    constructor( //Para dos variables
        name: String,
        var1Name: String,
        var2Name: String,
        calculate: (Double, Double) -> List<Double>
    ) : this(
        name = name,
        variableNames = listOf(var1Name, var2Name),
        calculate = { inputs -> calculate(inputs[var1Name]!!, inputs[var2Name]!!) }
    )

    constructor(
        name: String,
        var1Name: String,
        var2Name: String,
        var3Name: String,
        calculate: (Double, Double, Double) -> List<Double>
    ) : this(
        name = name,
        variableNames = listOf(var1Name, var2Name, var3Name),
        calculate = { inputs -> calculate(inputs[var1Name]!!, inputs[var2Name]!!, inputs[var3Name]!!) }
    )
}