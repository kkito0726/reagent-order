package kkito.reagent_order.app_user.model

class Email private constructor(val value: String) {
    init {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (!emailRegex.matches(value)) {
            throw IllegalArgumentException("無効なEmail形式です: $value")
        }
    }

    companion object {
        fun of(value: String): Email {
            return Email(value)
        }
    }

    override fun toString(): String = value
}