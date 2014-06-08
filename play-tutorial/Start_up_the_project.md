# 开始项目

## 介绍

在这个教程中，你将通过从头到尾开发一个真正的Web应用来学习Play框架，在这个应用中，我们将尝试用上你将在真实项目中需要的每样技术，同时介绍Play应用开发的最佳实践。

我们把教程分割成相互独立的各部分。每个部分将介绍更为复杂的特性，并提供真实项目需要的每样东西：验证，错误处理，框架安全，自动化测试组件，一个高大上的用户界面，一个管理面板，等等。

在本教程中的所有代码都能被用于你的项目。我们鼓励你复制粘贴代码片段，整份搬去用也没所谓。

## 项目

我们将开发（又一个）博客引擎。这不是一个非常有想象力的选择，但是它将允许我们探索开发一个现代Web应用的大多数技术。

为了让过程更加燃一点，我们将对不同的角色（编辑者，管理员）设置不同的权限。

这个博客引擎将以**yabe**为名。

![yabe](image/guide1-0.png)

>这个教程也已作为范例随下载包赠送。你可以在*samples-and-tests/yabe/*文件夹下找到成品代码。

## 前提

首先，确保你已经安装了Java平台。Play需要**Java 5或以上**的版本。

由于我们将需要频繁使用命令行，最好还是使用类Unix的操作系统。如果你用的是Windows，那也没问题；就是打命令的时候需要打多一点。

我们将假定你已经有Java和Web开发（特别是HTML，CSS和Javascript）经验。不过，你不需要熟悉JavaEE所有组件。Play是一个全栈Java框架，它提供了或者封装了你将需要的全部Java API。没用必要知道如何配置JPA实体管理器或部署一个JavaEE组件。

你将需要一个文本编辑器。如果更喜欢使用大而全的Java IDE，比如Eclipse或NetBeans，你当然可以用它。不过即使使用简单的文本编辑器，比如Textmate，Emacs或Vim，你也可以玩转Play。因为框架本身会处理好编译和部署的过程。我们很快就会看到这一点了……

在教程的后面，我们将使用Lighttpd和MySQL来展示如何在生产环境部署一个Play应用。但即使你没用安装这些，play也可以运行，这不是个问题。

## 安装Play

安装过程如丝般顺滑。从下载页面下载最新的二进制包，然后在你喜欢的地方解压它。

>如果你用的是Windows，最好避免在路径中混入空格。比如*c:\play*就是个比*c:\Documents And Settings\user\play*更好的选择。

为了方便操作，你需要添加Play文件夹到你的系统路径中。这样你就不需要在play命令前面敲一大通路径名了。要想检查安装是否成功，打开一个新的命令行窗口，敲下`play`；应该会出来play的基本使用帮助。

## 创建项目

现在Play已经安好了，是时候开始写博客应用。创建一个Play应用非常简单，仅需要play命令行工具。之后会生成Play应用的基本架构。

打开一个新的命令行并敲入：

    ~$ play new yabe
    
它会提醒输入应用的全名。输入**Yet Another Blog Engine**。

![new app](image/guide1-1.png)

`play new`命令创建了一个新的文件夹*yabe/*外加一系列文件和文件夹。其中包括下面各部分：

**app/** 包括应用的核心，划分为models，controllers和views文件夹。它也可以包括其他Java的包。这是`.java`源代码文件所在之处。

**conf/** 包括所有的应用配置文件，特别是主*application.conf*文件，路由定义文件和用于国际化的信息文件。

**lib/** 包括所有可选的Java库，比如标准的`.jar`。

**public/** 包括所有可以公开的资源，比如Javascript文件，样式表和图片。

**test/** 包括所有的应用测试。测试可以是Java的JUnit测试或者Selenium测试。

>因为Play只使用UTF-8编码，故所有的文本文件都需要使用UTF-8编码。确保你的文本编辑器已经做了相应的配置。

如果你开发过Java应用，你可能会奇怪`.class`文件到哪儿去了。答案是……没有`.class`文件了：Play并不使用任何`class`文件；相反它直接处理Java源代码。实际上我们使用Eclipse的编译器来即时编译Java源代码。

这导致了开发过程中的两点重要的改进。第一个，Play会自动监测Java源代码的改变并在运行时自动重载。第二个，当一个Java异常发生时，Play能向你展示更好的错误报告 - 带对应的源代码的哦～

>事实上Play在应用的*tmp/*文件夹下有字节码的缓存，但只用于加速重新启动项目的过程。如果需要，你可以用`play clean`清空缓存。

## 运行应用

现在看一下新创建的应用。回到命令行，切换到新创建的*yabe/*文件夹并输入`play run`。Play将加载应用，并在`localhost:9000`启动一个服务器。

用浏览器打开<http://localhost:9000>，你将看到新的应用。一个新的应用用一个标准的欢迎页面，告诉你一切安好。

![welcome](image/guide1-2.png)

下面看看一个新应用是如何展示这个页面的。

你的应用的主入口是`conf/routes`文件。这个文件定义了应用中所有合法的URL。如果你打开这个文件，就会看到第一个'route':

    GET		/			Application.index
    
这里简单地告诉Play，当服务器收到对`/`路径的一个**GET**请求，它要调用Java方法`Application.index`。在这个例子中，`Application.index`是`controllers.Application.index`的缩写，因为`controllers`包是默认在内的。

当你创建单个Java应用时，你通常用main方法定义一个单一入口：

    public static void main(String[] args) {
      ... 
    }
    
Play应用有多个入口，每个URL对应一个。我们称之为`action`方法。`action`方法定义于被称为`controllers`的类中。

让我们看一下`controllers.Application`控制器长什么样子。打开`yabe/app/controllers/Application.java`:

    package controllers;
     
    import play.mvc.*;
     
    public class Application extends Controller {
     
	    public static void index() {
		    render();
	    }
     
    }
    
注意控制器类拓展了`play.mvc.Controller`。这个类提供了许多对控制器有用的方法，比如我们在index action用到的`render()`。


这个index action被定义为`public static void`方法。action方法都需要这样定义。你可以看到这些方法都是static的，因为控制器类永远不会实例化。它们同时也是public的，这样框架才能调用它们来响应URL请求。它们总是返回void。

这个index action很简单：它调用`render()`方法来渲染模板。使用模板是大多数情况下生成HTTP响应的方式。（但不是唯一的方式）

模板是位于`/app/views`的文本文件。因为我们没有指定一个模板，这个action就会使用默认的`Application/index.html`。

打开`/yabe/app/views/Application/index.html`看看模板长啥样:

    #{extends 'main.html' /}
    #{set title:'Home' /}
     
    #{welcome /}

模板的内容简单明了。事实上，你看到的全部是Play标签。Play标签就像是JSP标签。#{welcome /}标签生成了你看到的欢迎信息。

\#{extends /}标签告诉Play，这个模板是继承自另一个叫`main.html`的模板。模板继承是强大的特性，它允许你通过重用组件来创建复杂的Web页面。

打开`/yabe/app/views/main.html`:

    <!DOCTYPE html>
    <html>
      <head>
        <title>#{get 'title' /}</title>
        <meta charset="${_response_encoding}">
        <link rel="stylesheet" media="screen"
          href="@{'/public/stylesheets/main.css'}">
        #{get 'moreStyles' /}
        <link rel="shortcut icon" type="image/png"
          href="@{'/public/images/favicon.png'}">
        <script type="text/javascript" charset="${_response_encoding}"
          src="@{'/public/javascripts/jquery-1.5.2.min.js'}"></script>
        #{get 'moreScripts' /}
      </head>
      <body>
        #{doLayout /}
      </body>
    </html>

你看到靠近底部的#{doLayout /}标签了吗？这是插入`Application/index.html`的内容的地方。

我们来看看Play如何自动重新加载控制器文件。打开`yabe/app/controllers/Application.java`，删掉`render()`后面的分号（来制造一个错误）

    public static void index() {
        render()
    }
    
在浏览器刷新页面。你可以看到Play监测到文件的变化，并重载了应用控制器。但因为制造了错误，你将看到一个编译错误。

![error](image/guide1-3.png)

Ok，现在更正错误，做一个真正的修改：

    public static void index() {
        System.out.println("Yop");
        render();
    }
    
这次，Play正确地重载了控制器，替换掉JVM中的旧代码。每次请求`/`都会在控制台输出'Yop'信息。

你可以移开那行输出代码，现在修改`yabe/app/views/Application/index.html`模板，替换掉欢迎信息：

    #{extends 'main.html' /}
    #{set title:'Home' /}
     
    <h1>A blog will be here</h1>

如果Java代码改变了，只需刷新页面，就能看到修改结果。

>我们还没有开始写博客应用呢。你可以选择使用文本编辑器或者IDE，比如Eclipse或NetBeans。如果你想使用IDE，参阅[配置你喜欢的IDE](http://play-framework.herokuapp.com/zh/ide)。

## 配置数据库

在开始码代码之前还要做多一件事。作为博客引擎，我们需要一个数据库。为了便于开发，Play内置了一个叫H2的数据库。当然如果需要，我们也可以切换到一个更加健壮的数据库。你可以选择设置数据是存储在内存中，还是在文件系统中（这样即使应用重新启动，你的数据也会保留）。

在一开始，我们将对应用模型做许多测试和改动。因此，最好选择存储在内存中，这样每次启动，都不会跟旧数据有任何牵连。

打开`yabe/conf/application.conf`，解除这一行的注释：

    db=mem

正如你在注释中看到的一样，你可以容易地配置任何JDBC数据库，甚至配置连接池。

>这个教程是按照纯内存数据库撰写的；如何在其他数据库使用JPA的内容超出了本教程的范畴。

现在，回到浏览器并刷新欢迎页面。Play将自动启动数据库。检查下面的一行是否出现在应用日志中：

    INFO  ~ Connected to jdbc:h2:mem:play
    
## 使用版本控制系统来追踪变化

当你开发一个项目时，最好使用版本控制系统（VCS）来存储你的源代码。如果发生什么问题，它允许你还原到上一个版本。同时它使得多人协作变得更方便，并且允许签出应用的各个版本。

当在VCS中存储Play应用代码时，记住把`tmp/`，`modules/`，`lib/`，`test-result/`和`logs`文件夹排除在外。

如果你正在使用Eclipse，和`play eclipsify`命令，那么你也应该把`.classpath`和`eclipse/`排除在外。

## Bazaar

这里我们将用Bazaar作为例子。[Bazaar](http://bazaar-vcs.org/)是一个分布式版本控制系统。

如何安装Bazaar超出了本教程的范畴，但无论是何种系统，安装都很简单。一旦你安装好了，切到博客文件夹，并输入命令初始化应用版本：

    $ bzr init
    $ bzr ignore tmp
    $ bzr ignore modules
    $ bzr ignore lib
    $ bzr ignore test-result
    $ bzr ignore logs
    
现在我们可以提交我们的博客引擎的第一个版本：

    $ bzr add
    $ bzr commit -m "YABE initial version"
    
## Git

[Git](http://git-scm.com/)是另一个分布式版本控制系统，查看它的文档来获取更多信息。

在应用的根目录下创建一个git版本库：

    $ git init
    
创建包括下面各项的`.gitignore`：

    /tmp
    /modules
    /lib
    /test-result
    /logs
    
加入应用的代码，然后提交：

    $ git add .
    $ git commit -m "YABE initial version"
    
版本1已经提交上去，现在我们的应用已经有了个坚实的基础。
