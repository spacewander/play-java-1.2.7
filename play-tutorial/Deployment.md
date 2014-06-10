# 部署应用

如今我们已经完成了博客引擎了。让我们来看一下一些部署Play应用的步骤。

## 定义一个框架ID

一般，你需要部署你的应用到一台跟开发时不一样的电脑。这台电脑（很有可能是台服务器）上面的Play安装包不会是一样的。

Play允许你给每个框架安装包指定不同的ID，来用同一个`application.conf`管理不同的配置。让我们假设应用将部署在`server01`上。

一旦框架已经安装在服务器上，用`play id`来定义一个框架ID：

    $ play id
    
并设置id为`server01`。现在我们可以在yabe的配置中定义只在服务器运行时起效的值。

## 在PROD模式下设置应用

我们首先给部署版本定义`application.mode`变量。目前，我们一直使用**DEV**来实现热重载，即时重新编译Java文件，显示错误信息。在**PROD**模式中，Play会在启动时编译所有的Java代码和模板，而且不会检查是否有改变。

在`yabe/conf/application.conf`定义：

    %server01.application.mode=PROD
    
现在当你在服务器运行yabe，它就会自动在PROD下启动。

## 配置MYySQL数据库

在生产环境中，我们将使用MySQL而不是一直在用的H2数据库。Play提供了MySQL配套的JDBC驱动，所以我们不需要下载别的。

编辑`yabe/conf/application.conf`中的数据库配置：

    %server01.db=mysql:root:secret@yabe
    
我们现在调整一下Hibernate管理数据库模式的方式。如果Java模型变动时，Hibernate能够自动更新数据库模式，那是再好不过的。

修改`jpa.ddl`配置键：

    %server01.db=mysql:root:secret@yabe

不过这么做恐怕会导致不可预料的后果，毕竟，在运行时吧数据库的安全交给未知的事物，这不是个好主意。如果你不希望Hibernate自动更新数据库，修改`jpa.ddl`配置键为`validate`：

    %server01.jpa.ddl=validate
    
## 配置HTTP服务器

目前我们仅仅是把80端口作为内置HTTP服务器的默认端口。但是这样做一台服务器上只能运行一个Play应用。如果我们需要在一台服务器上运行不同的应用（不过使用的是不同的IP地址），我们需要使用一个HTTP服务器作为反向代理。

你可以选择喜欢的HTTP服务器并配置其作为反向代理。这里我们选择轻量级的[lighttpd](http://www.lighttpd.net/)作为例子。

对lighttpd的详细配置已经超出了本教程的范围，但大体上看上去像这样：

    server.modules = (
          "mod_access",
          "mod_proxy",
          "mod_accesslog" 
    )
    …
    $HTTP["host"] =~ "www.yabe.com" {
        proxy.balance = "round-robin" proxy.server = ( "/" =>
            ( ( "host" => "127.0.0.1", "port" => 9000 ) ) )
    }
    
然后在`application.conf`中加入下面一行，让本地的反向代理能够连接上你的Play应用：

    %server01.XForwardedSupport=127.0.0.1

# 这才只是个开始

> 如果一路上你一直跟着本教程，你应该已经懂得如何开发一个Play应用了。你学到了Play开发所需的大部分概念。

但有许多特性我们还没谈论到，特别是跟Web服务相关的，比如JSON或XML。Play还有一些带来更多特性的模块没有讲到。并且Play本身也在不断发展着。

如果你认为Play能够使得你的开发如虎添翼，现在把握机会开始吧！

谢谢收看！
