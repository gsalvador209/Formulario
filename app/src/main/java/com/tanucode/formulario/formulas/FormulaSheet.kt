package com.tanucode.formulario.formulas

import com.tanucode.formulario.R
import com.tanucode.formulario.utils.StringProvider
import kotlin.math.sin
import kotlin.math.sqrt

class FormulaSheet(private val stringProvider: StringProvider) {
    val formulaNames: List<String> by lazy{
        listOf(
            quadraticFormula.name,
            parabolicHeight.name,
            r3Magnitude.name,
            potentialEnergy.name
        )
    }


    fun getFormula(index: Int): Formula? = when(index) {
        0-> quadraticFormula
        1-> parabolicHeight
        2-> r3Magnitude
        3-> potentialEnergy
        else -> null
    }

    // Existing quadratic formula
    private val quadraticFormula =
        Formula(
            name = stringProvider.getString(R.string.quadratic_formula_name),
            var1Name = stringProvider.getString(R.string.variable_a),
            var2Name = stringProvider.getString(R.string.variable_b),
            var3Name = stringProvider.getString(R.string.variable_c),
            calculate = { a, b, c ->
                val discriminant = b * b - 4 * a * c
                when {
                    discriminant < 0 -> emptyList() // No hay raices
                    else -> listOf(
                        (-b + sqrt(discriminant)) / (2 * a),
                        (-b - sqrt(discriminant)) / (2 * a)
                    )
                }
            }
        )



    private val parabolicHeight =
    Formula(
        name = stringProvider.getString(R.string.parabolic_height_name),
        var1Name = stringProvider.getString(R.string.initial_velocity),
        var2Name = stringProvider.getString(R.string.angle),
        var3Name = stringProvider.getString(R.string.gravity),
        calculate = { v0, angleDegrees, g ->
            val angleRad = Math.toRadians(angleDegrees)
            val height = (v0 * v0 * sin(angleRad) * sin(angleRad)) / (2 * g)
            listOf(height)
        }
    )


    private val r3Magnitude =
        Formula(
            name = stringProvider.getString(R.string.r3_magnitude_name),
            var1Name = stringProvider.getString(R.string.x_component),
            var2Name = stringProvider.getString(R.string.y_component),
            var3Name = stringProvider.getString(R.string.z_component),
            calculate = { x, y, z ->
                val magnitude = sqrt(x * x + y * y + z * z)
                listOf(magnitude)
            }
        )


    private val potentialEnergy =
        Formula(
            name = stringProvider.getString(R.string.potential_energy_name),
            var1Name = stringProvider.getString(R.string.mass),
            var2Name = stringProvider.getString(R.string.height),
            var3Name = stringProvider.getString(R.string.gravity),
            calculate = { m, h, g -> // Changed parameter order to match variableNames
                listOf(m * g * h)
            }
        )

}