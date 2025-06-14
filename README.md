# navigation
   a sample demo for navigation by using Baidu Map SDK.
# ‌项目概述‌：
    该项目主要为实现短距离骑行自动规划导航路线，实时位置更新功能

# ‌使用指南‌：
    该项目依赖组件使用了aliyun镜像。
    该项目打包后最低运行在SDK24（Android 7.0）版本以及以上手机系统。
    该项目运行目标系统为SDK35（Android 15）版本以及以上手机系统
    该项目编译Gradle版本为8.10.2，IED版本为Android Studio Ladybug Feature Drop | 2024.2.2
# ‌项目简介
    1、该项目基于百度地图SDK实现。
    2、该项目实现了启动APP后，自动开始定位，不会再次刷新实时地理位置，需要重新启动APP再次刷新（也可以通过修改MainActivity.mapHasShowCurrentLocation属性调整为实时动态更新）。
    3、该项目目前仅实现单一目的地选择功能（选择第二个地点，第一个目的地会被第二个覆盖）
    4、该项目目的地支持选择已标记建筑物地点，也可以选择未标记地点（例如某一条道路路边）
    5、目前仅实现骑行导航引擎，后续待实现可以新增步行、驾车等导航引擎。
    6、该项目支持导航偏离重新规划路线功能以及GPS信号变更、抵达目的地等提示信息。
    7、导航过程可以支持提示抵达目的地距离，耗时，并支持实时刷新。
    8、导航结束会有整体道路相关信息简报，例如距离、耗时、路径踪迹等

# 快速启动
    下载方式：
        GIT：https://github.com/Smither01/navigation.git
        ZIP：https://github.com/Smither01/navigation#

# APP运行截图
![img.png](img.png)
![img_1.png](img_1.png)