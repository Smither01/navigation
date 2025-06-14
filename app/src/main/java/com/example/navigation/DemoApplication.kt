package com.example.navigation

import android.app.Application
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.common.BaiduMapSDKException

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        // 默认隐私政策接口初始化方法
        val sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        val ifAgree = sp.getBoolean(SP_KEY, false)
        if (ifAgree) {
            SDKInitializer.setAgreePrivacy(this, true)
        } else {
            SDKInitializer.setAgreePrivacy(this, false)
        }

        try {
            SDKInitializer.initialize(this)
        } catch (e: BaiduMapSDKException) {
        }
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)
    }

    companion object {
        var SP_NAME: String = "privacy"

        var SP_KEY: String = "ifAgree"
    }
}