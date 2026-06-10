# DATA LINK

### 一、介绍
datalink是一个在各种异构数据源之间实现数据采集、桥接的工具，支持最少两个节点的集群模式。

目前支持的数据源有：

MQTT、Kafka、RabbitMQ、RocketMQ、ActiveMQ、Pulsar、Mysql、PostgreSQL、SQL Server、TDengine、TimescaleDB、MariaDB、Redis、TCP、UDP、HTTP、OPC UA、SNMP、Modbus TCP


支持无转换透传、JavaScript脚本、Java插件、SQL语句四种数据转换方式。

### 二、打包&安装

datalink是跨平台的，支持 Linux、Unix、macOS 以及 Windows，这意味着datalink可以部署在 x86_64 架构的服务器上。

#### 源码打包

```bash
git clone https://gitee.com/liyang9512/datalink.git
cd datalink
mvn -Prelease-datalink -Dmaven.test.skip=true clean install -U
```

#### 安装

使用源码打包或下载 datalink-server-$version.zip 或 datalink-server-$version.tar.gz 包

```bash
unzip datalink-server-$version.zip 或者 tar -xvf datalink-server-$version.tar.gz
cd datalink/bin
```

### 三、启动&停止

单节点模式：
```bash
cd datalink/bin

#windows start
startup.cmd

#linux start
sh startup.sh

#windows shutdown
shutdown.cmd

#linux shutdown
sh shutdown.sh
```

集群模式：（需在配置文件中配置节点列表）
```bash
cd datalink/bin

#windows start
startup.cmd -m cluster

#linux start
sh startup.sh -m cluster

#windows shutdown
shutdown.cmd

#linux shutdown
sh shutdown.sh
```

### 四、Dashboard

程序启动后，使用浏览器访问 http://127.0.0.1:9966/ 即可打开管理页面，
默认用户名：datalink   密码：aaaaaa

![img.png](datalink-ui/public/img.png)

### 五、配置文件

主要配置说明：

```bash
### 用于访问管理页面的端口,默认9966
server.port=9966

### 集群模式下节点列表配置,形式为 IP:端口,IP:端口,IP:端口
### 第一组IP端口必须为本节点的IP端口
datalink.cluster.member.list=
```