# MultiScreenUDP
Android platform multi screen interaction, the use of UDP protocol and its broadcast mechanism
====
<br>
实现了在局域网段内Android平台设备的搜寻，并与搜索到的设备互相通信交互。
<br>
该项目采用UDP的广播机制进行设备搜寻。主机端不断的在局域网中发UDP广播，
<br>
当其他设备收到广播时给予响应。之后可进行诸如通过终端输入显示在大屏、
<br>
终端请求大屏显示弹窗的等交互动作，详见demo代码。
Use
====
```xml
<!--register service-->
<service android:name="com.jiajunhui.udp_service.UDPService"/>
```
<br>
```java
//start service
private void startService() {
        Intent intent = new Intent(this, UDPService.class);
        startService(intent);
}
```
