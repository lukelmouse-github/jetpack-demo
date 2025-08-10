package com.example.feature.login.manager;

/**
 * 用户管理类
 * 负责用户信息的存储、获取和状态管理
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006J\b\u0010\u000b\u001a\u0004\u0018\u00010\u0007J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u000fJ\u0010\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007J\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u00132\u0006\u0010\u0014\u001a\u00020\u0015R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/example/feature/login/manager/UserManager;", "", "()V", "USER_DATA", "", "liveData", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/core/common/model/User;", "mmkv", "Lcom/tencent/mmkv/MMKV;", "getLoginLiveData", "getUser", "isLogin", "", "removeUser", "", "saveUser", "user", "start", "Landroidx/lifecycle/LiveData;", "context", "Landroid/content/Context;", "feature-login_debug"})
public final class UserManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String USER_DATA = "user_data";
    @org.jetbrains.annotations.NotNull()
    private static com.tencent.mmkv.MMKV mmkv;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<com.example.core.common.model.User> liveData = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.feature.login.manager.UserManager INSTANCE = null;
    
    private UserManager() {
        super();
    }
    
    /**
     * 获取用户登录状态 LiveData
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<com.example.core.common.model.User> getLoginLiveData() {
        return null;
    }
    
    /**
     * 获取当前用户信息
     */
    @org.jetbrains.annotations.Nullable()
    public final com.example.core.common.model.User getUser() {
        return null;
    }
    
    /**
     * 保存用户信息
     */
    public final void saveUser(@org.jetbrains.annotations.Nullable()
    com.example.core.common.model.User user) {
    }
    
    /**
     * 判断用户是否已登录
     */
    public final boolean isLogin() {
        return false;
    }
    
    /**
     * 移除用户信息（退出登录）
     */
    public final void removeUser() {
    }
    
    /**
     * 启动登录页面
     */
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.core.common.model.User> start(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}