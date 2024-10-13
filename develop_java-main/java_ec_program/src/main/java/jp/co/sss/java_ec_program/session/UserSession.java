package jp.co.sss.java_ec_program.session;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import jp.co.sss.java_ec_program.entity.Users;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private Users user;
    private long cartItemCount; // カート内のアイテム数

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return this.user != null;
    }

    public void logout() {
        this.user = null;
    }
    
    // getter and setter
    public long getCartItemCount() {
        return cartItemCount;
    }

    public void setCartItemCount(long cartItemCount) {
        this.cartItemCount = cartItemCount;
    }
}