package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private val inputQueue: Queue<String> = LinkedList() // FIFO queue for storing inputs
    private var currentInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

        val buttons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
            R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
            R.id.btn_8, R.id.btn_9, R.id.btn_dot
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onNumberClick((it as Button).text.toString()) }
        }

        findViewById<Button>(R.id.btn_clear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btn_delete).setOnClickListener { delete() }
        findViewById<Button>(R.id.btn_add).setOnClickListener { onOperatorClick("+") }
        findViewById<Button>(R.id.btn_subtract).setOnClickListener { onOperatorClick("-") }
        findViewById<Button>(R.id.btn_multiply).setOnClickListener { onOperatorClick("×") }
        findViewById<Button>(R.id.btn_divide).setOnClickListener { onOperatorClick("÷") }
        findViewById<Button>(R.id.btn_equals).setOnClickListener { calculateResult() }
    }

    private fun onNumberClick(number: String) {
        currentInput += number
        updateDisplay()
    }

    private fun onOperatorClick(op: String) {
        if (currentInput.isNotEmpty()) {
            inputQueue.add(currentInput) // Add current number to the queue
            inputQueue.add(op) // Add operator to the queue
            currentInput = "" // Reset current input
            updateDisplay()
        }
    }

    private fun calculateResult() {
        if (currentInput.isNotEmpty()) {
            inputQueue.add(currentInput) // Add the last number to the queue
            currentInput = ""
        }

        val result = evaluateQueue()
        currentInput = result.toString()
        updateDisplay()
        inputQueue.clear() // Clear the queue after calculation
    }

    private fun evaluateQueue(): Double {
        if (inputQueue.isEmpty()) return 0.0

        var result = inputQueue.poll().toDoubleOrNull() ?: return 0.0

        while (inputQueue.isNotEmpty()) {
            val operator = inputQueue.poll()
            val nextOperand = inputQueue.poll()?.toDoubleOrNull() ?: 0.0

            result = when (operator) {
                "+" -> result + nextOperand
                "-" -> result - nextOperand
                "×" -> result * nextOperand
                "÷" -> if (nextOperand != 0.0) result / nextOperand else return 0.0 // Prevent division by zero
                else -> result
            }
        }

        return result
    }

    private fun clear() {
        currentInput = ""
        inputQueue.clear()
        updateDisplay()
    }

    private fun delete() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        display.text = currentInput.ifEmpty { "0" }
    }
}
