package com.example.navigation.ui.theme

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.baidu.mapapi.bikenavi.BikeNavigateHelper
import com.baidu.mapapi.bikenavi.adapter.IBNaviStatusListener
import com.baidu.mapapi.bikenavi.adapter.IBRouteGuidanceListener
import com.baidu.mapapi.bikenavi.adapter.IBikeNaviLocationListener
import com.baidu.mapapi.bikenavi.model.BikeNaviLocationResult
import com.baidu.mapapi.bikenavi.model.BikeRouteDetailInfo
import com.baidu.mapapi.bikenavi.model.IBRouteIconInfo
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.walknavi.model.RouteGuideKind
import com.example.navigation.R


class NavigationActivity : ComponentActivity() {
    private val TAG  = "MainActivity"
    private lateinit var mNaviHelper: BikeNavigateHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigationctivity)

        //获取BikeNavigateHelper示例
        mNaviHelper = BikeNavigateHelper.getInstance()

        // 获取诱导页面地图展示View
        try {
            val view: View = mNaviHelper.onCreate(this@NavigationActivity)
            if (view == null) {
                Toast.makeText(this@NavigationActivity, getString(R.string.navigation_failed), Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            setContentView(view)

        }catch (exception: Exception){
            Log.d(TAG,"create mapview failed:"+exception.message)
        }



        // 开始导航
        mNaviHelper.startBikeNavi(this@NavigationActivity)
        mNaviHelper.setRouteGuidanceListener( this, object: IBRouteGuidanceListener{
            override fun onRouteGuideIconInfoUpdate(p0: IBRouteIconInfo?) {
            }

            override fun onRouteGuideIconUpdate(p0: Drawable?) {
            }

            override fun onRouteGuideKind(p0: RouteGuideKind?) {
            }

            override fun onRoadGuideTextUpdate(p0: CharSequence?, p1: CharSequence?) {
            }

            override fun onRemainDistanceUpdate(p0: CharSequence?) {
            }

            override fun onRemainTimeUpdate(p0: CharSequence?) {
            }

            override fun onGpsStatusChange(p0: CharSequence?, p1: Drawable?) {
                Toast.makeText(this@NavigationActivity, p0, Toast.LENGTH_SHORT).show()
            }

            override fun onRouteFarAway(p0: CharSequence?, p1: Drawable?) {
            }

            override fun onRoutePlanYawing(p0: CharSequence?, p1: Drawable?) {
                Toast.makeText(this@NavigationActivity, getString(R.string.replanning_path_navigation), Toast.LENGTH_SHORT).show()

            }

            override fun onReRouteComplete() {
                Toast.makeText(this@NavigationActivity, getString(R.string.replanning_path_success), Toast.LENGTH_SHORT).show()
            }

            override fun onArriveDest() {
                Toast.makeText(this@NavigationActivity, getString(R.string.navigation_end), Toast.LENGTH_SHORT).show()
            }

            override fun onVibrate() {
            }

            override fun onGetRouteDetailInfo(p0: BikeRouteDetailInfo?) {
            }

            override fun onNaviLocationUpdate() {
            }

        })
        mNaviHelper.setBikeNaviStatusListener(object : IBNaviStatusListener{
            override fun onNaviExit() {
                Log.d(TAG,"onNaviExit")
            }

        })

        mNaviHelper.setNaviLocationListener(object :IBikeNaviLocationListener{
            override fun onNaviLocationUpdate(p0: BikeNaviLocationResult?) {
                if (p0 == null){
                    return
                }
                Log.d(TAG,"当前位置: ${p0.gpsLatitude}, ${p0.gpsLongitude}")
            }

        })

    }

    override fun onResume() {
        super.onResume()
        mNaviHelper.resume()
    }

    override fun onPause() {
        super.onPause()
        mNaviHelper.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNaviHelper.quit()
    }
}