package com.fidelapi.cardnumberfield

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * A component to handle card number input.
 * It includes features that help to make sure that the card number is valid.
 */
class CardNumberField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    /**
     * The card number input by the user.
     */
    var cardNumber: String? = null
        set(value) {
            field = value
            textInputEditText.setText(value)
        }
        get() = textInputEditText.text.toString()

    /**
     * Sets the hint text on the input layout.
     */
    var hint: String = "Your card number"
        set(value) {
            field = value
            textInputLayout.hint = value
        }

    /**
     * Sets a color on the hint text.
     */
    var hintColor: Int? = null
        set(value) {
            field = value
            setHintColorValue(value)
        }

    /**
     * Sets a custom style on the layout hint
     */
    var hintStyle: Int = R.style.InputHintTextSmall
        set(value) {
            field = value
            textInputLayout.setHintTextAppearance(value)
        }

    /**
     * Sets the return key type (actionGo | actionDone | etc). For possible values, check the android:imeOptions docs.
     * Default value is `EditorInfo.IME_ACTION_NEXT`.
     * @see <a href="https://developer.android.com/reference/android/widget/TextView#attr_android:imeOptions">
     *     android:imeOptions docs
     *     </a>.
     */
    var imeOptions: Int = EditorInfo.IME_ACTION_DONE
        set(value) {
            field = value
            textInputEditText.imeOptions = value
        }

    /**
     * Set this property, in order to display an error message.
     */
    private var error: String? = null
        set(value) {
            field = value
            showError(value)
        }

    private var textInputLayout: TextInputLayout
    private var textInputEditText: TextInputEditText

    private val errorColor = ContextCompat.getColor(context, R.color.fdl_error_color)

    init {
        inflate(context, R.layout.view_standard_input, this)

        textInputLayout = findViewById(R.id.standardInputLayout)
        textInputEditText = findViewById(R.id.standardInputEditText)
        textInputEditText.addTextChangedListener(CreditCardTextWatcher())
        textInputLayout.hint = hint

        setDefaultValues()
    }

    /**
     * Sets the default values which the view starts with:
     * Text size; IME Options; Hint Style; The custom Text Watcher
     */
    private fun setDefaultValues() {
        textInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.fdl_text_size))
        textInputEditText.imeOptions = imeOptions
        textInputLayout.isHintEnabled = true
        textInputLayout.errorIconDrawable = null
        textInputLayout.setHintTextAppearance(hintStyle)
    }

    /**
     * Adds a text watcher to the text field.
     */
    fun addTextChangeListener(textWatcher: TextWatcher) {
        textInputEditText.addTextChangedListener(object: CreditCardTextWatcher() {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
                textWatcher.beforeTextChanged(s, start, count, after)
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                textWatcher.onTextChanged(s, start, before, count)
            }
            override fun afterTextChanged(s: Editable) {
                super.afterTextChanged(s)
                textWatcher.afterTextChanged(s)
            }
        })
    }

    /**
     * Adds a text watcher to the text field.
     */
    fun onEditorAction(listener: (String) -> Unit) {
        textInputEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == imeOptions) {
                listener.invoke(textView.text.toString())
            }
            true
        }
    }

    /**
     * Sets a OnEditorActionListener listener on the text input
     */
    fun onEditorAction(listener: TextView.OnEditorActionListener) {
        textInputEditText.setOnEditorActionListener(listener)
    }

    /**
     * Sets the on key listener on the text input
     */
    override fun setOnKeyListener(listener: OnKeyListener) {
        textInputEditText.setOnKeyListener(listener)
    }

    private fun setHintColorValue(color: Int?) {
        if (color != null) {
            textInputLayout.hintTextColor = ColorStateList.valueOf(color)
        }
    }

    private fun showError(error: String?) {
        val shouldShowError = !error.isNullOrEmpty()
        if (shouldShowError) {
            textInputLayout.error = " "
        }
        val hintColorToSet = if (shouldShowError) errorColor else hintColor
        setHintColorValue(hintColorToSet)
        textInputLayout.isErrorEnabled = shouldShowError
        textInputLayout.hint = if (shouldShowError) error else hint
    }
}