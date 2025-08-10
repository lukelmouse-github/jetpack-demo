package com.example.feature.login.ui;

/**
 * 登录页面
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B\u0005\u00a2\u0006\u0002\u0010\u0004J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u000fH\u0002J\b\u0010\u0011\u001a\u00020\u000fH\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0013H\u0002J\b\u0010\u0015\u001a\u00020\u0013H\u0016J\b\u0010\u0016\u001a\u00020\u0013H\u0002J\b\u0010\u0017\u001a\u00020\u0013H\u0002J\u0012\u0010\u0018\u001a\u00020\u00132\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0007\u001a\u00020\u00028VX\u0096\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t\u00a8\u0006\u001b"}, d2 = {"Lcom/example/feature/login/ui/LoginActivity;", "Lcom/example/core/common/base/BaseActivity;", "Lcom/example/feature/login/ui/LoginViewModel;", "Lcom/example/feature/login/databinding/ActivityLoginBinding;", "()V", "mData", "Lcom/example/feature/login/ui/LoginLayoutBean;", "mViewModel", "getMViewModel", "()Lcom/example/feature/login/ui/LoginViewModel;", "mViewModel$delegate", "Lkotlin/Lazy;", "getLayoutResId", "", "getPassword", "", "getSurePassword", "getUserName", "initData", "", "initEditText", "initView", "loginAction", "registerAction", "saveUserInfo", "user", "Lcom/example/core/common/model/User;", "feature-login_debug"})
public final class LoginActivity extends com.example.core.common.base.BaseActivity<com.example.feature.login.ui.LoginViewModel, com.example.feature.login.databinding.ActivityLoginBinding> {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy mViewModel$delegate = null;
    private com.example.feature.login.ui.LoginLayoutBean mData;
    
    public LoginActivity() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.example.feature.login.ui.LoginViewModel getMViewModel() {
        return null;
    }
    
    @java.lang.Override()
    public int getLayoutResId() {
        return 0;
    }
    
    @java.lang.Override()
    public void initData() {
    }
    
    @java.lang.Override()
    public void initView() {
    }
    
    /**
     * 执行登录操作
     */
    private final void loginAction() {
    }
    
    /**
     * 执行注册操作
     */
    private final void registerAction() {
    }
    
    /**
     * 保存用户信息并关闭页面
     */
    private final void saveUserInfo(com.example.core.common.model.User user) {
    }
    
    /**
     * 获取用户名
     */
    private final java.lang.String getUserName() {
        return null;
    }
    
    /**
     * 获取密码
     */
    private final java.lang.String getPassword() {
        return null;
    }
    
    /**
     * 获取确认密码
     */
    private final java.lang.String getSurePassword() {
        return null;
    }
    
    /**
     * 初始化输入框（切换模式时清空）
     */
    private final void initEditText() {
    }
}