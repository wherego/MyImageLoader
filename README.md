# MyImageLoader 
[![Build Status](https://travis-ci.org/WrongChao/MyImageLoader.svg?branch=master)](https://travis-ci.org/WrongChao/MyImageLoader)

## 简介

三级缓存图片加载器,分别由文件缓存、内存未解码图片缓存，内存Bitmap缓存三部分构成。

### 文件缓存
文件缓存采用的是Google推荐类DiskLRUCache，稍加修改后直接使用。

### 内存未解码图片缓存
内存未解码图片缓存用LRUCache类来实现。其缓存内容为从文件缓存中读取的图片的byteArray，
特点是相比较Bitmap而言，未解码的图片占用内存要小得多（比如，png格式图片最高图片质量压缩的情况下，其大小要比解码后的bitmap小50倍左右）。

### 内存Bitmap缓存
内存Bitmap缓存中的对象都是可以直接使用的bitmap对象。

## 工作原理

1. 当用户请求一个image时，首先在主线程中通过其url直接检索Bitmap缓存，如果缓存命中，则返回Bitmap。
2. 如果Bitmap缓存未命中，则将请求向下层缓存分配.
- 将查询任务放入线程池中异步执行(cpu核心数＋1个核心线程，cpu核心数*2＋1个最大线程，具体配置与AsyncTask的THREAD_POOL_EXECUTOR相同)。
- 首先检查内存未解码缓存是否命中，若命中则加载图片并发送message到主线程looper，将bitmap设置给ImageView。
- 若未命中，则从DiskLRUCache中请求内容。若DiskLRUCache命中，则读区bitmap并更新Bitmap缓存及内存未解码图片缓存，然后如第二步中发送message至looper。
- 若DiskLRUCache未命中，则从网络上请求资源，得到资源后更新三个缓存，并发送message至looper，将bitmap设置给ImageView。

## Demo

例子中我用了50个图片作测试，图片链接来自百度图片，侵删。
例子中还包括对GridView加载图片作的优化（当view滑动速度过快时停止加载），有效解决了卡顿问题。　

## 后期计划

- 加入OKHttp支持
- 加入自动调整缓存大小功能

