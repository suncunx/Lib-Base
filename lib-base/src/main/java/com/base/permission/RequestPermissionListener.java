package com.base.permission;

public interface RequestPermissionListener {

    /**
     *
     * @param permissions 权限名称
     * @param granted 该权限是否允许
     * @param shouldShowRationale 是否应该解释申请该权限的原因， 用户上次已经拒绝过此权限申请并且没有选中不再询问时值为true
     */
    void onRequestPermissionResult(String[] permissions, boolean[] granted, boolean[] shouldShowRationale);
}
