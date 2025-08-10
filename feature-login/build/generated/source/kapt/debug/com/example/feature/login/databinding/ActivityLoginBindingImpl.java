package com.example.feature.login.databinding;
import com.example.feature.login.R;
import com.example.feature.login.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityLoginBindingImpl extends ActivityLoginBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.close, 5);
        sViewsWithIds.put(R.id.userName, 6);
        sViewsWithIds.put(R.id.password, 7);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityLoginBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 8, sIncludes, sViewsWithIds));
    }
    private ActivityLoginBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.Button) bindings[3]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.TextView) bindings[4]
            , (android.widget.EditText) bindings[7]
            , (android.widget.EditText) bindings[2]
            , (android.widget.TextView) bindings[1]
            , (android.widget.EditText) bindings[6]
            );
        this.btnLogin.setTag(null);
        this.featureName.setTag(null);
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.surePassword.setTag(null);
        this.tips.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.bean == variableId) {
            setBean((com.example.feature.login.ui.LoginLayoutBean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setBean(@Nullable com.example.feature.login.ui.LoginLayoutBean Bean) {
        updateRegistration(0, Bean);
        this.mBean = Bean;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.bean);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeBean((com.example.feature.login.ui.LoginLayoutBean) object, fieldId);
        }
        return false;
    }
    private boolean onChangeBean(com.example.feature.login.ui.LoginLayoutBean Bean, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.login) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        int beanLoginViewGONEViewVISIBLE = 0;
        boolean beanLogin = false;
        com.example.feature.login.ui.LoginLayoutBean bean = mBean;
        java.lang.String beanFeatureName = null;
        java.lang.String beanTips = null;
        java.lang.String beanBtnText = null;

        if ((dirtyFlags & 0x7L) != 0) {



                if (bean != null) {
                    // read bean.login
                    beanLogin = bean.isLogin();
                }
            if((dirtyFlags & 0x7L) != 0) {
                if(beanLogin) {
                        dirtyFlags |= 0x10L;
                }
                else {
                        dirtyFlags |= 0x8L;
                }
            }


                // read bean.login ? View.GONE : View.VISIBLE
                beanLoginViewGONEViewVISIBLE = ((beanLogin) ? (android.view.View.GONE) : (android.view.View.VISIBLE));
            if ((dirtyFlags & 0x5L) != 0) {

                    if (bean != null) {
                        // read bean.featureName
                        beanFeatureName = bean.getFeatureName();
                        // read bean.tips
                        beanTips = bean.getTips();
                        // read bean.btnText
                        beanBtnText = bean.getBtnText();
                    }
            }
        }
        // batch finished
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.btnLogin, beanBtnText);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.featureName, beanFeatureName);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.tips, beanTips);
        }
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            this.surePassword.setVisibility(beanLoginViewGONEViewVISIBLE);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): bean
        flag 1 (0x2L): bean.login
        flag 2 (0x3L): null
        flag 3 (0x4L): bean.login ? View.GONE : View.VISIBLE
        flag 4 (0x5L): bean.login ? View.GONE : View.VISIBLE
    flag mapping end*/
    //end
}