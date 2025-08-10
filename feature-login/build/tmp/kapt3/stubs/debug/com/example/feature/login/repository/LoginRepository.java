package com.example.feature.login.repository;

/**
 * 登录数据仓库
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\'\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J/\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0010H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0015R#\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0016"}, d2 = {"Lcom/example/feature/login/repository/LoginRepository;", "Lcom/example/feature/login/repository/BaseRepository;", "retrofit", "Lretrofit2/Retrofit;", "(Lretrofit2/Retrofit;)V", "api", "Lcom/example/feature/login/api/RequestCenter;", "kotlin.jvm.PlatformType", "getApi", "()Lcom/example/feature/login/api/RequestCenter;", "api$delegate", "Lkotlin/Lazy;", "login", "Lcom/example/feature/login/network/NetResult;", "Lcom/example/core/common/model/User;", "username", "", "password", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "surePassword", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-login_debug"})
public final class LoginRepository extends com.example.feature.login.repository.BaseRepository {
    @org.jetbrains.annotations.NotNull()
    private final retrofit2.Retrofit retrofit = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    
    public LoginRepository(@org.jetbrains.annotations.NotNull()
    retrofit2.Retrofit retrofit) {
        super();
    }
    
    private final com.example.feature.login.api.RequestCenter getApi() {
        return null;
    }
    
    /**
     * 用户登录
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object login(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.feature.login.network.NetResult<com.example.core.common.model.User>> $completion) {
        return null;
    }
    
    /**
     * 用户注册
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object register(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String surePassword, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.feature.login.network.NetResult<com.example.core.common.model.User>> $completion) {
        return null;
    }
}