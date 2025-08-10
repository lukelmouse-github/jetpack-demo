package com.example.feature.login.repository;

/**
 * 基础数据仓库类
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002JA\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00050\u0004\"\u0004\b\u0000\u0010\u00052\"\u0010\u0006\u001a\u001e\b\u0001\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00050\u00040\b\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0007H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ+\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00050\u0004\"\u0004\b\u0000\u0010\u00052\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00050\fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u000e"}, d2 = {"Lcom/example/feature/login/repository/BaseRepository;", "", "()V", "callRequest", "Lcom/example/feature/login/network/NetResult;", "T", "call", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "handleResponse", "response", "Lcom/example/feature/login/network/BaseModel;", "(Lcom/example/feature/login/network/BaseModel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "feature-login_debug"})
public class BaseRepository {
    
    public BaseRepository() {
        super();
    }
    
    /**
     * 处理网络请求调用
     */
    @org.jetbrains.annotations.Nullable()
    public final <T extends java.lang.Object>java.lang.Object callRequest(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super com.example.feature.login.network.NetResult<? extends T>>, ? extends java.lang.Object> call, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.feature.login.network.NetResult<? extends T>> $completion) {
        return null;
    }
    
    /**
     * 处理网络响应
     */
    @org.jetbrains.annotations.Nullable()
    public final <T extends java.lang.Object>java.lang.Object handleResponse(@org.jetbrains.annotations.NotNull()
    com.example.feature.login.network.BaseModel<T> response, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.feature.login.network.NetResult<? extends T>> $completion) {
        return null;
    }
}