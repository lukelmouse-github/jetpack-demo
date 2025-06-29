package com.example.common.base;

import com.example.common.R;
import com.example.common.databinding.ActivityEmptyShellBinding;
import com.example.common.log.ALog;
import com.example.common.routers.RouterPath;
import com.therouter.router.Autowired;
import com.therouter.router.Route;

@Route(path = RouterPath.EMPTY_SHELL_ACTIVITY, description = "Fragment容器页")
public class EmptyShellActivity extends BaseActivity<ActivityEmptyShellBinding> {

    @Autowired
    BaseFragment fragment;

    public EmptyShellActivity() {
        super(R.layout.activity_empty_shell);
    }

    @Override
    protected void initView() {
        super.initView();
        if (fragment == null) {
            ALog.e("EmptyShellActivity 的 fragment 为 null 无法跳转");
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }
}

/**
 *
 * TODO 偶现的问题..., 查一下,是demo 的问题,还是theRouter的Bug.
 * E  (EmptyShellActivity.java:24)#initView-[M][Thread:main-2]
 *                                            EmptyShellActivity 的 fragment 为 null 无法跳转
 * 02:39:33.443 Engine                     E  Initializing failure
 *                                            java.lang.NullPointerException: Attempt to read from field 'java.lang.String androidx.fragment.app.Fragment.mPreviousWho' on a null object reference in method 'void androidx.fragment.app.FragmentTransaction.doAddOp(int, androidx.fragment.app.Fragment, java.lang.String, int)'
 *                                            	at androidx.fragment.app.FragmentTransaction.doAddOp(FragmentTransaction.java:299)
 *                                            	at androidx.fragment.app.BackStackRecord.doAddOp(BackStackRecord.java:195)
 *                                            	at androidx.fragment.app.FragmentTransaction.replace(FragmentTransaction.java:400)
 *                                            	at androidx.fragment.app.FragmentTransaction.replace(FragmentTransaction.java:350)
 *                                            	at com.example.common.base.EmptyShellActivity.initView(EmptyShellActivity.java:27)
 *                                            	at com.drake.engine.base.EngineActivity.init(EngineActivity.kt:48)
 *                                            	at com.drake.engine.base.EngineActivity.setContentView(EngineActivity.kt:43)
 *                                            	at androidx.activity.ComponentActivity.onCreate(ComponentActivity.java:371)
 *                                            	at androidx.fragment.app.FragmentActivity.onCreate(FragmentActivity.java:216)
 *                                            	at com.drake.engine.base.FinishBroadcastActivity.onCreate(FinishBroadcastActivity.kt:32)
 *                                            	at com.example.common.base.BaseActivity.onCreate(BaseActivity.kt:29)
 *                                            	at android.app.Activity.performCreate(Activity.java:8595)
 *                                            	at android.app.Activity.performCreate(Activity.java:8573)
 *                                            	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1456)
 *                                            	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
 *                                            	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3922)
 *                                            	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:103)
 *                                            	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:139)
 *                                            	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:96)
 *                                            	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2443)
 *                                            	at android.os.Handler.dispatchMessage(Handler.java:106)
 *                                            	at android.os.Looper.loopOnce(Looper.java:205)
 *                                            	at android.os.Looper.loop(Looper.java:294)
 *                                            	at android.app.ActivityThread.main(ActivityThread.java:8177)
 *                                            	at java.lang.reflect.Method.invoke(Native Method)
 *                                            	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
 */