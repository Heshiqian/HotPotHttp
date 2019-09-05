# HotPotHttp 火锅Http

 `version 0.0.1` (仅预览 )

## 功能

- 基础静态HTTP服务器
- HTTP插件，可自定义HTTP功能(目前只支持Socket自定义返回数据)
- 多线程并发

## 使用方式

**目前仅可预览，可进行普通HTTP请求文件(GET请求)，运行时需生成`website`目录在** *当前运行目录* **下，在website目录下放置静态文件即可在浏览器相对路径访问此文件。**

1、下载项目
    `git clone https://github.com/Heshiqian/HotPotHttp.git`

2、将 `hotpothttp-core` 项目作为gradle项目依赖于其他Java项目

例如以下`build.gradle`所示：

```
plugins {
    id 'java'
}

group 'heshiqian.cn'
version '1.0-SNAPSHOT'

sourceCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
    compile project(':hotpothttp-core')//此处依赖hotpothttp-core项目
}
```

3、创建自己的启动类，继承自 `HotPotHttpApplicationStarter`

```java
public class MyStarter extends HotPotHttpApplicationStarter {
    public static void main(String[] args) {
        run(args,MyStarter.class);
    }
}
```

4、无需其他设置，运行即可，运行成功将显示以下信息

```
[main] setup environment.
[main] create website folder... path:E:\JavaProject\HotPotHttpNew\website
[main] not found outside configure file, will use internal properties
[main] port = [8080], ip = [127.0.0.1]
[main] start scanning plugin...
[main] no plugin find, pass.
[Thread-1] pre start core thread.
[hotpot-worker-thread-1] - has been create.
[Thread-1] Server is running on port:8080
[Thread-1] Please access "http://127.0.0.1:8080/", for more detail
[HotPot Daemon Thread] thread start in 2019-07-24 16:35:20 -->  id : 12
```

此时访问`http://127.0.0.1:8080/`即可（可访问website目录下的文件）

## 计划中功能

- JAVA NIO 服务模式
- 更多插件类型支持

