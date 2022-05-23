package com.fidelapi.cardnumberfield

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import java.security.InvalidParameterException

internal open class CreditCardTextWatcher() : TextWatcher {
    var changingText = false
    var cursorPos = 0
    var editVelocity = 0

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!changingText) {
            editVelocity = count - before
            cursorPos = start + count
        }
    }

    override fun afterTextChanged(s: Editable) {
        if (!changingText) {
            changingText = true
            setText(s)
            changingText = false
        }
    }

    private fun setText(s: Editable) {
        val formattedText: String = formatForViewing(s)
        s.replace(0, s.length, formattedText)
        val i = cursorPos
        val formattedTextLength = formattedText.length
        if (cursorPos >= formattedTextLength) {
            cursorPos = formattedTextLength
        }
        if (editVelocity > 0
            && cursorPos > 0
            && formattedText[-1 + cursorPos] == ' '
        ) {
            cursorPos += 1
        }
        if (editVelocity < 0
            && cursorPos > 1
            && formattedText[-1 + cursorPos] == ' '
        ) {
            cursorPos -= 1
        }
        if (cursorPos != i) {
            Selection.setSelection(s, cursorPos)
        }
    }

    private fun formatForViewing(cardNumber: CharSequence): String {
        // make sure the cc isn't null
        if (cardNumber == null) {
            throw InvalidParameterException("cannot have null credit card number")
        }

        // clean up the string
        val cleanedCardNumber: String = clean(cardNumber)

        // make sure the format isn't null or empty
        val format: IntArray = intArrayOf(4, 4, 4, 4)
        if (format == null || format.size == 0) {
            throw InvalidParameterException("cannot have null or empty credit card format")
        }

        // sum the children and make sure it only contains numbers greater than zero
        var sum = 0
        for (i in format) {
            if (i <= 0) {
                throw InvalidParameterException("the pattern must contain numbers greater than zero")
            }
            sum += i
        }

        // make sure the string is long enough
        var length = cleanedCardNumber.length
        if (length <= 0) {
            return cleanedCardNumber
        }
        if (length > sum) {
            length = sum
        }
        return format(cleanedCardNumber, length, format)
    }

    private fun format(cleanedNumber: String, length: Int, format: IntArray): String {
        val builder = StringBuilder()
        var start = 0
        var end = 0
        for (p in format) {
            end += p
            val isAtEnd = end >= length
            builder.append(cleanedNumber.substring(start, if (isAtEnd) length else end))
            start += if (!isAtEnd) {
                builder.append(" ")
                p
            } else {
                break
            }
        }
        return builder.toString()
    }

    private fun clean(cardNumber: CharSequence?): String {
        return cardNumber.toString().trim { it <= ' ' }.replace("[^\\d]".toRegex(), "")
    }
}