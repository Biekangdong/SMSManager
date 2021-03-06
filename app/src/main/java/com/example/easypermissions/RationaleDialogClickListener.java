package com.example.easypermissions;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;

/**
 * Click listener for either {@link RationaleDialogFragment} or {@link RationaleDialogFragmentCompat}.
 */
public class RationaleDialogClickListener implements Dialog.OnClickListener {

    private Object mHost;
    private RationaleDialogConfig mConfig;
    public pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks mCallbacks;

    RationaleDialogClickListener(RationaleDialogFragmentCompat compatDialogFragment,
                                 RationaleDialogConfig config,
                                 pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks callbacks) {

        mHost = compatDialogFragment.getParentFragment() != null
                ? compatDialogFragment.getParentFragment()
                : compatDialogFragment.getActivity();

        mConfig = config;
        mCallbacks = callbacks;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    RationaleDialogClickListener(RationaleDialogFragment dialogFragment,
                                 RationaleDialogConfig config,
                                 pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks callbacks) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mHost = dialogFragment.getParentFragment() != null ?
                    dialogFragment.getParentFragment() :
                    dialogFragment.getActivity();
        } else {
            mHost = dialogFragment.getActivity();
        }

        mConfig = config;
        mCallbacks = callbacks;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            EasyPermissions.executePermissionsRequest(mHost,
                    mConfig.permissions, mConfig.requestCode);
        } else {
            notifyPermissionDenied();
        }
    }

    private void notifyPermissionDenied() {
        if (mCallbacks != null) {
            mCallbacks.onPermissionsDenied(mConfig.requestCode,
                    Arrays.asList(mConfig.permissions));
        }
    }
}
