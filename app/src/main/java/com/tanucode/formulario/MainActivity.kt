package com.tanucode.formulario

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.tanucode.formulario.databinding.ActivityMainBinding
import com.tanucode.formulario.formulas.Formula
import com.tanucode.formulario.formulas.FormulaSheet
import com.tanucode.formulario.utils.AndroidStringProvider

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var formulaSheet: FormulaSheet

    private var isSpinneInitialCall : Boolean = true
    private var currentFormula : Formula? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        formulaSheet = FormulaSheet(AndroidStringProvider(this))
        setupSpinner()
        setupCalculateButton()
        setupInputListeners()
    }

    private fun setupInputListeners(){
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }
        }

        binding.etVarA.addTextChangedListener(textWatcher)
        binding.etVarB.addTextChangedListener(textWatcher)
        binding.etVarC.addTextChangedListener(textWatcher)
    }

    private fun setupSpinner() {
        val formulaNames = listOf("") + formulaSheet.formulaNames
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_selectable_list_item,
            formulaNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spFormula.adapter = adapter

        binding.spFormula.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(isSpinneInitialCall){
                    isSpinneInitialCall = false
                    setVisibleFields(0)
                    return
                }
                clearFields()
                currentFormula = formulaSheet.getFormula(position-1) //Se ignora el elemento vacio

                currentFormula?.let { formula ->
                    setVisibleFields(formula.variableNames.size)
                    updateFieldsLabels(formula)
                    if (position == 4 || position == 2){ //Para la energía potencial
                        binding.etVarC.setText("9.81")
                    }
                    validateInputs()
                } ?: run {
                    setVisibleFields(0)
                    validateInputs()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }

    private fun clearFields(){
        // Clear input fields
        binding.etVarA.text?.clear()
        binding.etVarB.text?.clear()
        binding.etVarC.text?.clear()

        // Clear results
        binding.tvFirstResult.text = ""
        binding.tvSecondResult.text = ""
        binding.tvSecondResult.visibility = View.GONE
    }

    private fun updateFieldsLabels(formula : Formula){
        formula.variableNames.forEachIndexed { index, label ->
            when(index){
                0 -> binding.tvVarA.text = label
                1 -> binding.tvVarB.text = label
                2 -> binding.tvVarC.text = label
            }
        }
    }

    private fun displayResults(results: List<Double>) {
        fun safeGet(index: Int): String = results.getOrNull(index)?.let {
            "%.4f".format(it)
        } ?: ""

        when (results.size) {
            0 -> {
                binding.tvFirstResult.text = getString(R.string.no_results)
                binding.tvSecondResult.visibility = View.GONE
            }
            1 -> {
                binding.tvFirstResult.text = getString(R.string.result_template, safeGet(0))
                binding.tvSecondResult.visibility = View.GONE
            }
            2 -> {
                binding.tvFirstResult.text = getString(R.string.result_template, safeGet(0))
                binding.tvSecondResult.text = getString(R.string.result_template, safeGet(1))
                binding.tvSecondResult.visibility = View.VISIBLE
            }
            else -> { // For formulas with >2 results
                binding.tvFirstResult.text = getString(R.string.multiple_results)
                binding.tvSecondResult.visibility = View.GONE
            }
        }
    }

    fun setVisibleFields(count: Int) {
        // Set visibility for A, B, C based on count
        binding.etVarA.visibility = if (count >= 1) View.VISIBLE else View.GONE
        binding.tvVarA.visibility = if (count >= 1) View.VISIBLE else View.GONE

        binding.etVarB.visibility = if (count >= 2) View.VISIBLE else View.GONE
        binding.tvVarB.visibility = if (count >= 2) View.VISIBLE else View.GONE

        binding.etVarC.visibility = if (count >= 3) View.VISIBLE else View.GONE
        binding.tvVarC.visibility = if (count >= 3) View.VISIBLE else View.GONE
    }



    private fun invokeSnackbar(text: String) {
        Snackbar.make(binding.main,text, Snackbar.LENGTH_SHORT)
            .setTextColor(getColor(R.color.white))
            .setBackgroundTint(getColor(android.R.color.holo_red_dark))
            .show()
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.isEnabled = false
        binding.btnCalculate.setOnClickListener {
            currentFormula?.let { formula ->
                try {
                    // Create input map matching the formula's expected variable names
                    val inputs = mutableMapOf<String, Double>().apply {
                        if (formula.variableNames.size >= 1) put(
                            formula.variableNames[0],
                            binding.etVarA.text.toString().toDouble()
                        )
                        if (formula.variableNames.size >= 2) put(
                            formula.variableNames[1],
                            binding.etVarB.text.toString().toDouble()
                        )
                        if (formula.variableNames.size >= 3) put(
                            formula.variableNames[2],
                            binding.etVarC.text.toString().toDouble()
                        )
                    }

                    // Execute the formula's calculation
                    val results = formula.calculate(inputs)

                    // Display the results
                    displayResults(results)

                } catch (e: NumberFormatException) {
                    invokeSnackbar(getString(R.string.invalid_input_error))
                } catch (e: Exception) {
                    invokeSnackbar(getString(R.string.calculation_error))
                }
            } ?: run {
                invokeSnackbar(getString(R.string.select_formula_error))
            }
        }
    }

    private fun validateInputs() {
        currentFormula?.let { formula ->
            val requiredFields = formula.variableNames.size //No todas las fromulas ocupan los 3 campos
            val fields = listOf(
                binding.etVarA.text.toString(),
                binding.etVarB.text.toString(),
                binding.etVarC.text.toString()
            )

            val allFilled = fields.take(requiredFields).all { it.isNotBlank() }
            binding.btnCalculate.isEnabled = allFilled
        } ?: run {
            binding.btnCalculate.isEnabled = false
        }
    }

}