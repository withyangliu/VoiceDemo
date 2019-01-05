package com.withyang.voicedemo.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PermissionHelper {

    private static final String TAG = "PermissionHelper";

    public final static int READ_PHONE_STATE_CODE = 101;
    public final static int WRITE_EXTERNAL_STORAGE_CODE = 102;
    public final static int RECORD_AUDIO_CODE = 103;
    public final static int ACCESS_FINE_LOCATION_CODE = 104;
    public final static int CAMERA_CODE = 105;
    public final static int READ_CONTACTS_CODE = 106;
    public final static int READ_PHONE_CODE = 107;
    public final static int WRITE_SETTINGSS = 108;
    private final static int REQUEST_OPEN_APPLICATION_SETTINGS_CODE = 12345;

    public static final PermissionModel PERMISSION_READ_PHONE_STATE = new PermissionModel(
            "电话",
            Manifest.permission.READ_PHONE_STATE,
            "我们需要读取手机信息的权限来标识您的身份",
            READ_PHONE_STATE_CODE);
    public static final PermissionModel PERMISSION_WRITE_EXTERNAL_STORAGE = new PermissionModel(
            "存储空间",
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "我们需要允许我们读写你的存储卡，以方便我们临时保存一些数据",
            WRITE_EXTERNAL_STORAGE_CODE);
    public static final PermissionModel PERMISSION_RECORD_AUDIO = new PermissionModel(
            "麦克风",
            Manifest.permission.RECORD_AUDIO,
            "我们需要允许使用麦克风的权限才能发送语音",
            RECORD_AUDIO_CODE);
    public static final PermissionModel PERMISSION_ACCESS_FINE_LOCATION = new PermissionModel(
            "位置",
            Manifest.permission.ACCESS_FINE_LOCATION,
            "我们需要允许访问位置信息的权限",
            ACCESS_FINE_LOCATION_CODE);
    public static final PermissionModel PERMISSION_CAMERA = new PermissionModel(
            "相机",
            Manifest.permission.CAMERA,
            "我们需要使用相机的权限",
            CAMERA_CODE);
    public static final PermissionModel PERMISSION_READ_CONTACTS = new PermissionModel(
            "通讯录",
            Manifest.permission.READ_CONTACTS,
            "我们需要访问通讯录的权限",
            READ_CONTACTS_CODE);
    public static final PermissionModel PERMISSION_READ_PHONE = new PermissionModel(
            "电话",
            Manifest.permission.READ_PHONE_STATE,
            "我们需要电话的权限",
            READ_PHONE_CODE);
    public static final PermissionModel WRITE_SETTINGS = new PermissionModel("", Manifest.permission.WRITE_SETTINGS, "", READ_PHONE_STATE_CODE);

    private PermissionModel[] mPermissionModels;

    private Activity mActivity;

    private OnResult mOnResult;

    public PermissionHelper(Activity activity, PermissionModel[] permissionModels) {
        mActivity = activity;
        mPermissionModels = permissionModels;
    }

    public void setmOnResult(OnResult mOnResult) {
        this.mOnResult = mOnResult;
    }

    public void applyPermissions() {
        try {
            for (final PermissionModel model : mPermissionModels) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission
                        (mActivity, model
                                .permission)) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{model.permission},
                            model.requestCode);
                    return;
                }
            }
            if (mOnResult != null) {
                mOnResult.onNext();
            }
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permissions[0])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                        .setTitle("权限申请")
                        .setMessage(findPermissionExplain(permissions[0]))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                applyPermissions();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mOnResult != null) {
                                    mOnResult.onCancel();
                                }
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                        .setTitle("权限申请")
                        .setMessage("请在打开的窗口的权限中开启" + findPermissionName(permissions[0]) +
                                "权限，以正常使用本应用")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openApplicationSettings(REQUEST_OPEN_APPLICATION_SETTINGS_CODE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mOnResult != null) {
                                    mOnResult.onCancel();
                                }
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            }
            return;
        }
        if (isAllRequestedPermissionGranted()) {
            if (mOnResult != null) {
                mOnResult.onNext();
            }
        } else {
            applyPermissions();
        }
    }

    private boolean openApplicationSettings(int requestCode) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse
                    ("package:" + mActivity
                            .getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            mActivity.startActivityForResult(intent, requestCode);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_APPLICATION_SETTINGS_CODE:
                if (isAllRequestedPermissionGranted()) {
                    if (mOnResult != null) {
                        mOnResult.onNext();
                    }
                } else {
                    if (mOnResult != null) {
                        mOnResult.onCancel();
                    }
                }
                break;
        }
    }

    public boolean isAllRequestedPermissionGranted() {
        for (PermissionModel model : mPermissionModels) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity,
                    model.permission)) {
                return false;
            }
        }
        return true;
    }

    private String findPermissionExplain(String permission) {
        if (mPermissionModels != null) {
            for (PermissionModel model : mPermissionModels) {
                if (model != null && model.permission != null && model.permission.equals
                        (permission)) {
                    return model.explain;
                }
            }
        }
        return null;
    }

    private String findPermissionName(String permission) {
        if (mPermissionModels != null) {
            for (PermissionModel model : mPermissionModels) {
                if (model != null && model.permission != null && model.permission.equals
                        (permission)) {
                    return model.name;
                }
            }
        }
        return null;
    }

    public static class PermissionModel {

        public String name;

        public String permission;

        public String explain;

        public int requestCode;

        public PermissionModel(String name, String permission, String explain, int requestCode) {
            this.name = name;
            this.permission = permission;
            this.explain = explain;
            this.requestCode = requestCode;
        }
    }

    public interface OnResult {
        void onNext();

        void onCancel();

    }

    /**
     * 检查权限
     *
     * @param onResult
     */
    public void checkPermission(OnResult onResult) {
        if (Build.VERSION.SDK_INT < 23) {
            this.setmOnResult(onResult);
            onResult.onNext();
        } else {
            this.setmOnResult(onResult);
            if (isAllRequestedPermissionGranted()) {
                onResult.onNext();
            } else {
                applyPermissions();
            }
        }
    }
}
