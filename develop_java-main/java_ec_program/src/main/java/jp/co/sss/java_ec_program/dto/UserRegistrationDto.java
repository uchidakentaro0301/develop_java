package jp.co.sss.java_ec_program.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {
	
  //ユーザー名
    @NotBlank(message = "ユーザー名が入力されていません")
    @NotEmpty
    private String userName;

  //メールアドレス
    @NotBlank(message = "メールアドレスが入力されていません")
    @Email(message = "有効なメールアドレスを入力してください")
    @NotEmpty
    private String email;

  //電話番号
    @NotBlank(message = "電話番号が入力されていません")
    @NotEmpty
    @Pattern(regexp = "^[0-9-]+$", message = "電話番号は半角数字とハイフンのみ使用できます")
    private String phone;

  //パスワード
    @NotBlank(message = "パスワードが入力されていません")
    @NotEmpty
    @Size(min = 8, message = "パスワードは少なくとも8文字以上でなければなりません")
    private String passwords;

  //パスワード（確認）
    @NotBlank(message = "パスワード（確認）が入力されていません")
    @NotEmpty
    private String passwordConfirmation;

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }
}