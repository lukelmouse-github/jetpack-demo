package com.example.demo.api;

import com.drake.logcat.LogCat;
import com.example.common.base.BaseFragment;
import com.example.common.routers.RouterPath;
import com.example.common.utils.UIUtils;
import com.example.demo.R;
import com.example.demo.api.contact.ContactAPI;
import com.example.demo.databinding.FragmentApiBinding;
import com.hjq.permissions.XXPermissions;
import com.therouter.router.Route;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

@Route(path = RouterPath.API, description = "API定义页面")
public class APIFragment extends BaseFragment<FragmentApiBinding> {
    List<ContactAPI.Contact> contactList = new ArrayList<>();
    public APIFragment() {
        super(R.layout.fragment_api);
    }

    @Override
    protected void initView() {
        super.initView();

        binding.btnGetContactList.setOnClickListener(v -> {
                contactList = getContactList(
                        UIUtils.parseInt(binding.etOffset.getText().toString(), 1),
                        UIUtils.parseInt(binding.etLimit.getText().toString(), 1)
                );
                showResult(contactList.toString());
            }
        );
    }

    private List<ContactAPI.Contact> getContactList(int offset, int limit) {
        // TODO 接入权限请求框架。看看能不能同步呢？
//        XXPermissions.with(getContext()).permission(Permission.READ_CONTACTS)
//                // 申请单个权限
//                .permission(Permission.Gr)
//                // 申请多个权限
//                .permission(Permission.Group.CALENDAR)
//                // 设置权限请求拦截器（局部设置）
//                //.interceptor(new PermissionInterceptor())
//                // 设置不触发错误检测机制（局部设置）
//                //.unchecked()
//                .request(new OnPermissionCallback() {
//
//                    @Override
//                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
//                        if (!allGranted) {
//                            toast("获取部分权限成功，但部分权限未正常授予");
//                            return;
//                        }
//                        toast("获取录音和日历权限成功");
//                    }
//
//                    @Override
//                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
//                        if (doNotAskAgain) {
//                            toast("被永久拒绝授权，请手动授予录音和日历权限");
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(context, permissions);
//                        } else {
//                            toast("获取录音和日历权限失败");
//                        }
//                    }
//                });
        return ContactAPI.getContactList(getContext().getContentResolver(), offset, limit);
    }

    private void showResult(String result) {
        LogCat.e(result);
        binding.tvResult.setText(result);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
