package com.example.navigation.ui.theme

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.baidu.mapapi.bikenavi.BikeNavigateHelper
import com.baidu.mapapi.bikenavi.model.BikeRouteResult
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.Overlay
import com.baidu.mapapi.map.OverlayOptions
import com.baidu.mapapi.map.PolylineOptions
import com.baidu.mapapi.model.LatLng
import com.example.navigation.R


class RouteResultActivity : ComponentActivity() {
    private lateinit var baiduView: MapView
    private lateinit var baiduMap: BaiduMap
    private lateinit var mNaviHelper: BikeNavigateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_result)
        baiduView = findViewById(R.id.baidu_map_view)
        baiduMap = baiduView.map
        setMapZoomLevel()
        mNaviHelper = BikeNavigateHelper.getInstance()
        var totleTime = intent.getLongExtra("totleTime",0)
        var destLocation = intent.getParcelableExtra<LatLng>("destLocation") as LatLng
        var routeResult = initMapCenter(destLocation)
        initRouteInfo(routeResult, totleTime)
    }

    private fun initMapCenter(destLocation: LatLng): BikeRouteResult? {
        val update = MapStatusUpdateFactory.newLatLng(destLocation)
        baiduMap.animateMapStatus(update)
        var routeResult = mNaviHelper.bikeNaviRouteInfo
        if (routeResult != null) {
            val mOverlayOptions: OverlayOptions = PolylineOptions()
                .width(10)
                .color(Color.GREEN)
                .points(routeResult.positions)
            val mPolyline: Overlay = baiduMap.addOverlay(mOverlayOptions)
        }
        return routeResult
    }

    @SuppressLint("SetTextI18n")
    private fun initRouteInfo(
        routeResult: BikeRouteResult?,
        totleTime: Long
    ) {
        val totleDis = findViewById<TextView>(R.id.totle_length)
        totleDis.text = routeResult!!.distance.toString() + "米"
        val totleTimeText = findViewById<TextView>(R.id.totle_time)
        totleTimeText.text = totleTime.toString() + "秒"
    }

    private fun setMapZoomLevel(){
        val builder: MapStatus.Builder = MapStatus.Builder()
        builder.zoom(20.0f)
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    override fun onResume() {
        super.onResume()
        baiduView.onResume()
    }

    override fun onPause() {
        super.onPause()
        baiduView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        baiduView.onDestroy()
    }
}