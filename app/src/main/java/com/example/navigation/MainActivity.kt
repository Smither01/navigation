package com.example.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.bikenavi.BikeNavigateHelper
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.map.Overlay
import com.baidu.mapapi.map.OverlayOptions
import com.baidu.mapapi.model.LatLng
import com.example.navigation.ui.theme.NavigationActivity


class MainActivity : ComponentActivity() {
    private val TAG  = "MainActivity"
    private lateinit var baiduView: MapView
    private lateinit var baiduMap: BaiduMap
    private lateinit var currentLocation: LatLng
    private lateinit var destLocation: LatLng
    private lateinit var locationClient: LocationClient
    private lateinit var lastOverlay: Overlay
    private var mapHasShowCurrentLocation = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            initLocation(locationClient)
        } else {
            Toast.makeText(this, "need granted all permission", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        baiduView = findViewById(R.id.baidu_map_view)
        findViewById<Button>(R.id.start_navigation).setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if (!::destLocation.isInitialized){
                    Toast.makeText(this@MainActivity, getString(R.string.select_dest), Toast.LENGTH_SHORT).show()
                    return
                }
                startNavigation()
            }

        })
        initNavigationMap()
    }

    override fun onResume() {
        super.onResume()
        baiduView.onResume()
        locationClient.start()
    }

    private fun initNavigationMap(){
        startLocationSelf()
        setMapZoomLevel()
        initMapSelectedPointFunction()
    }

    private fun startLocationSelf(){
        baiduMap = baiduView.map
        baiduMap.isMyLocationEnabled = true
        LocationClient.setAgreePrivacy(true)
        locationClient = LocationClient(this)
        locationClient.registerLocationListener(MyLocationListener())

        val permissionList = getNeededPermission()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionList.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionList.toTypedArray())
        } else {
            initLocation(locationClient)
        }
    }

    private fun getNeededPermission(): MutableList<String> {
        val permissionList = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE)
        }
        return permissionList
    }

    private fun setMapZoomLevel(){
        val builder: MapStatus.Builder = MapStatus.Builder()
        builder.zoom(18.0f)
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
    }

    private fun initLocation(locationClient: LocationClient) {
        Log.d(TAG,"start init location")
        val option = LocationClientOption()
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy)
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        option.setIsNeedAddress(true)
        option.setOpenGps(true)
        locationClient.setLocOption(option)
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //mapView 销毁后不在处理新接收的位置
            Log.d(TAG,"on receive location")
            if (!::baiduView.isInitialized){
                Log.d(TAG,"map should initialize first")
                return
            }

          /*  if (mapHasShowCurrentLocation){
                Log.d(TAG,"map has updated")
                return
            }*/

            mapHasShowCurrentLocation = true
            val locData = MyLocationData.Builder()
                .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.direction).latitude(location.latitude)
                .longitude(location.longitude).build()
            currentLocation = LatLng(location.latitude, location.longitude)
            baiduMap.setMyLocationData(locData)
            if (location.locType == BDLocation.TypeGpsLocation || location.locType == BDLocation.TypeNetWorkLocation) {
                val center = com.baidu.mapapi.model.LatLng(location.latitude, location.longitude)
                val update = MapStatusUpdateFactory.newLatLng(center)
                baiduMap.animateMapStatus(update)
            }
        }
    }

    private fun initMapSelectedPointFunction(){
        baiduMap.setOnMapClickListener(object : BaiduMap.OnMapClickListener {
            override fun onMapClick(latLng: LatLng) {
                // 获取点击的经纬度
                val latitude = latLng.latitude
                val longitude = latLng.longitude
                Log.d(TAG,"选中的位置: $latitude, $longitude")
                onSelectPointInMap(latLng)
            }

            override fun onMapPoiClick(poi: MapPoi?) {
                // 点击了地图上的兴趣点
                poi?.let {
                    val latitude = it.position.latitude
                    val longitude = it.position.longitude
                    onSelectPointInMap(LatLng(latitude, longitude))
                    Log.d(TAG,"选中的兴趣点: ${it.name}")
                }
            }
        })
    }

    private fun onSelectPointInMap(latLng: LatLng){
        destLocation = latLng
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_point)
        //构建MarkerOption，用于在地图上添加Marker
        val option: OverlayOptions = MarkerOptions()
            .position(latLng)
            .icon(bitmap)
        //在地图上添加Marker，并显示,保持本次选中的目标
        if (::lastOverlay.isInitialized) {
            baiduMap.removeOverLays(mutableListOf(lastOverlay))
        }
        lastOverlay = baiduMap.addOverlay(option)
    }

    override fun onPause() {
        super.onPause()
        baiduView.onPause()
        locationClient.stop()
    }

    private fun initNavigationEngine(){
        // 获取导航控制类
        // 引擎初始化
        SDKInitializer.setAgreePrivacy(applicationContext,true)
        BikeNavigateHelper.getInstance().initNaviEngine(this, object : IBEngineInitListener {
            override fun engineInitSuccess() {
                //骑行导航初始化成功之后的回调
                Log.d(TAG,"导航引擎初始化成功")
                startCalculatePath()
            }

            override fun engineInitFail() {
                //骑行导航初始化失败之后的回调
                Log.d(TAG,"导航引擎初始化失败")
            }
        })
    }

    private fun startNavigation(){
        initNavigationEngine()
    }

    private fun startCalculatePath(){
        //构造BikeNaviLaunchParam
        //.vehicle(0)默认的普通骑行导航
        val startLocation = BikeRouteNodeInfo()
        startLocation.location = currentLocation
        val endLocation = BikeRouteNodeInfo()
        endLocation.location = destLocation
        val bikeParam = BikeNaviLaunchParam().startNodeInfo(startLocation).endNodeInfo(endLocation).vehicle(0)

        //发起算路
        BikeNavigateHelper.getInstance()
            .routePlanWithRouteNode(bikeParam, object : IBRoutePlanListener {
                override fun onRoutePlanStart() {
                    //执行算路开始的逻辑
                }

                override fun onRoutePlanSuccess() {
                    //算路成功
                    //跳转至诱导页面
                    startRealNavigation()
                }

                override fun onRoutePlanFail(bikeRoutePlanError: BikeRoutePlanError) {
                    //执行算路失败的逻辑
                }
            })
        }

    private fun startRealNavigation(){
        val intent: Intent = Intent(
            this@MainActivity,
            NavigationActivity::class.java
        )
        startActivity(intent)
    }

    private fun showHistoryPath(){
        BikeNavigateHelper.getInstance().bikeNaviRouteInfo
    }
}
