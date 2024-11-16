package kkito.reagent_order.app_user.model

class AppUserName private constructor(val value: String) {
    init {
        if (value.length !in 3..15) {
            throw IllegalArgumentException("Userネームは3文字以上15文字以下で入力してください")
        }
    }

    companion object {
        fun of(value: String): AppUserName {
            return AppUserName(value)
        }
    }

    override fun toString(): String {
        return value
    }
}