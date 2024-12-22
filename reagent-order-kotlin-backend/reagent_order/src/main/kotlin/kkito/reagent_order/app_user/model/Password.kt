package kkito.reagent_order.app_user.model

@JvmInline
value class Password(val value: String) {
    init {
        if (value.length <= 5) {
            throw IllegalArgumentException("パスワードは10文字以上で入力してください")
        }
    }
}