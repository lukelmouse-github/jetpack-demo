package com.example.feature.login.ui;

/**
 * 登录视图模型
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nJ$\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lcom/example/feature/login/ui/LoginViewModel;", "Lcom/example/core/common/base/BaseViewModel;", "loginRepo", "Lcom/example/feature/login/repository/LoginRepository;", "(Lcom/example/feature/login/repository/LoginRepository;)V", "loginLiveData", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/core/common/model/User;", "login", "username", "", "password", "register", "surePassword", "feature-login_debug"})
public final class LoginViewModel extends com.example.core.common.base.BaseViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.feature.login.repository.LoginRepository loginRepo = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<com.example.core.common.model.User> loginLiveData = null;
    
    public LoginViewModel(@org.jetbrains.annotations.NotNull()
    com.example.feature.login.repository.LoginRepository loginRepo) {
        super();
    }
    
    /**
     * 用户登录
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<com.example.core.common.model.User> login(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    /**
     * 用户注册
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<com.example.core.common.model.User> register(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String surePassword) {
        return null;
    }
}