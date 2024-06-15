# 说明

本项目后端采取 Spring Boot + MyBatis Plus + Lombok 进行开发，前端使用 vue3

前后端分离，通信均采用 json 格式

为了便于协作，采用 apifox 对 api 进行具体记录，大家可以加入团队 online-banking-system https://app.apifox.com/invite?token=sDvWHbIYAOXNu8HzsfqDp

# 目录说明

`resources/application.yml` 为项目的基本配置，具体内容可见参数名。

`resources/db/init.sql` 为 初始化schema 的 sql 命令，`data-init.sql` 为 插入初始数据的命令

源代码主要分成三层，controller 直接处理 api，mapper 作为数据库操作的封装，entity 里为操作的实体，例如 user 等，

service 是 controller 和 mapper 之间的层，各模块的函数，主要是避免裸的数据库操作以及便于模块间互相调用。

文件夹内有占位的空 java 文件可忽视

# 补充

后端的返回格式统一使用 `utils/RespResult` 的格式，payload 中为返回值，err 为错误信息，code 为错误代码

# 测试相关

## 如何运行

现在已全部在main分支

~~分别clone下整个仓库在两个文件夹内，一个在`dev`分支上运行后端，一个在`dev_front`上运行前端~~

## 后端运行

前置：需要电脑上安装有 mysql 和 redis

在 application.yml 中编辑端口、账户、密码等相关信息，之后直接运行即可

## 前端运行

进入`frontend` 目录后，先 `npm install`，随后 `npm run serve`

## 默认数据

有5个用户账号: a1, a2, a3, a4, a5，密码均为 123456

1个系统管理员账号 admin，密码 123456

每个组内部小管理员账号为 a + 小组序号（按照说明里的顺序），密码为 123456

每个用户账号都有存折和卡

# 反馈问题

## 模块内问题

如果是模块内的问题且不是由整合的过程中修改产生的，请自行修改。

修改完往github上发起 pull request（注意，修改的是前端请将你的dev_front分支的代码向dev_front分支发送PR，后端类似

**必使用pr不要发zip，zip我们会难以比较修改的地方（敲重点）(git反正后面也会用到，而且学一些基本的操作很快的)**

## 模块间问题

如果是模块间问题，可以直接在共享文档上编辑问题，fbz和zjl会根据问题进行修改

**问题描述请参考以下格式以方便我们修改**

```
模块名
前端问题/后端问题/不确定
对应代码文件
对应那个api（url）
具体的问题描述
相关截图
```
