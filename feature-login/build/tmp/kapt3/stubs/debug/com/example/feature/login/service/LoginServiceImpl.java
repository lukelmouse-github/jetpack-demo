package com.example.feature.login.service;

/**
 * 登录服务具体实现类
 */
@com.therouter.inject.Singleton()
@com.therouter.inject.ServiceProvider()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0016J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016J\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\f\u001a\u00020\rH\u0016\u00a8\u0006\u000e"}, d2 = {"Lcom/example/feature/login/service/LoginServiceImpl;", "Lcom/example/core/common/service/LoginService;", "()V", "getLiveData", "Landroidx/lifecycle/LiveData;", "Lcom/example/core/common/model/User;", "getUserInfo", "isLogin", "", "removeUserInfo", "", "start", "context", "Landroid/content/Context;", "feature-login_debug"})
public final class LoginServiceImpl implements com.example.core.common.service.LoginService {
    
    public LoginServiceImpl() {
        super();
    }
    
    @java.lang.Override()
    public boolean isLogin() {
        return false;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public com.example.core.common.model.User getUserInfo() {
        return null;
    }
    
    @java.lang.Override()
    public void removeUserInfo() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.lifecycle.LiveData<com.example.core.common.model.User> start(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.lifecycle.LiveData<com.example.core.common.model.User> getLiveData() {
        return null;
    }
}