package kkito.reagent_order.error

enum class ErrorCode(val code: String, val message: String) {
    E0001("E-0001", "ユーザーネームは3文字以上15文字以下で入力してください"),
    E0002("E-0002", "無効なEmail形式です"),
    E0003("E-0003", "パスワードは10文字以上で入力してください"),
    E0004("E-0004", "入力いただいたユーザーネームはすでに使用されています"),
    E0005("E-0005", "入力いただいたメールアドレスはすでに使用されています"),
}
