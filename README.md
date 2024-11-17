## 项目介绍

22年搞的大创用的项目。管理员后台界面找不到了。

后端Springboot，前端微信小程序，实现中药图片识别、中药单分析主治功效等、相似图片检索。使用DJL在java端调用pytorch训练好的模型，对上传的图片进行识别，完成中药识别、特征提取（用于相似图片检索）。使用ElasticSearch实现全文检索和相似图片检索的向量检索，以及中药成分分析检索。

> 架构图

<img src="./assets/wps1.jpg" alt="img" style="zoom:150%;" />

## 效果展示（原图找不到，word里复制出来的）

> 中药识别界面

![img](./assets/wps2.jpg)

> 药单分析界面

![img](./assets/wps3.jpg)

> 图片搜索界面

![image-20240625120019213](./assets/image-20240625120019213.png)

> 其他杂七杂八图片

![img](./assets/wps4.jpg)

![img](./assets/wps5.jpg)

![img](./assets/wps6.jpg)

![img](./assets/wps7.jpg)

## 数据集相关

当时直接python爬虫爬的百度图片自行构建数据集，类别有以下种类：

```bash
麻黄
桂枝
荆芥
防风
银花
连翘
薄荷
天麻
钩藤
全蝎
蜈蚣
附子
干姜
肉桂
吴茱萸
高良姜
石膏
大黄
麻仁
黄芩
黄连
黄柏
龙胆草
柴胡
香附
苍术
白术
藿香
木香
砂仁
熟地
茯苓
泽泻
薏苡仁
车前子
滑石
半夏
陈皮
浙贝母
瓜蒌
川贝母
百合
寄生
川断
杜仲
威灵仙
羌活
独活
桔梗
前胡
杏仁
枳壳
厚朴
川芎
丹参
牛膝
仙鹤草
大黄炭
地榆炭
荆芥炭
生地
玄参
内金
山楂
神曲
麦芽
党参
黄芪
山药
当归
白芍
阿胶
山萸
麦冬
沙参
仙灵脾
淫羊藿
益智仁
肉豆蔻
肉苁蓉
```

如果你有自己的数据集，在java的iherb-herb中resources里，修改names.txt，注意行号对应类别label。

## 数据库相关

jsoup爬的某个网站，忘了是哪个，在我记得相关代码在iherb-herb的test中。

## 注意事项

模型不想传了，自行训练吧，我中药识别用的efficientnet，可以换个轻量点的模型，比如mobilenet shufflenet ghostnet什么的。

提取特征我用的预训练的resnet吧。注意模型导出的时候参考下面的文章，不过DJL更新这么久了，你可以自己去他们官网看看文档啥的。

文章关键词提取随便用ik分词器糊弄了一下，自定义关键词词典，然后提取文章中出现频率最高的x个词语。

文字识别用的百度OCR，自行申请API，免费。

**细节看我CSDN发的文章：**

[Java调用Pytorch模型进行图像识别](https://blog.csdn.net/m0_52640724/article/details/124565688?spm=1001.2014.3001.5502)

[Java+ElasticSearch+Pytorch实现以图搜图](https://blog.csdn.net/m0_52640724/article/details/129357847?spm=1001.2014.3001.5502)

## 联系方式

代码可能比较乱，如果需要我在深度学习什么的方面帮忙，可以有偿提供帮助，vx：real_suicideboys
