# HarmonyMix
安卓应用如何混合打包拥有鸿蒙特性？: https://developer.huawei.com/consumer/cn/blog/topic/03637432111020021
基于Java的脑筋急转弯服务卡片: https://developer.huawei.com/consumer/cn/forum/topic/0201643417383460227?fid=0101303901040230869 （审核中）

Contants.API_KEY https://www.tianapi.com/ 中申请账号后可得到，自行申请去吧，免费接口每天可以使用100次。

代码下载下来运行在模拟器需要做的两件事(运行真机,需要另行签名,添加udid):
1. 根目录build.gradle中的signingConfigs需要进行变更.(在 File/Project Structure/signing Configs 进行文件位置的重新选择)
2. Contants.API_KEY需要自行申请, 若是你只是想看效果, 可以把 两处 requestKid(); 都换成 updateForms(null);
   后者会变成时间的更新（自动、主动）
   
   
   内容: 自动更新后，主动点击更新。
   ![CardRefresh](https://user-images.githubusercontent.com/32134764/129433151-36ba5fe1-c9ad-4ca8-adf3-6e3c1a2110ec.gif)
