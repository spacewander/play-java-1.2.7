# 创建工程

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

The play new command creates a new directory yabe/ and populates it with a series of files and directories, the most important being:

app/ contains the application’s core, split between models, controllers and views directories. It can contain other Java packages as well. This is the directory where .java source files live.

conf/ contains all the configuration files for the application, especially the main application.conf file, the routes definition files and the messages files used for internationalization.

lib/ contains all optional Java libraries packaged as standard .jar files.

public/ contains all the publicly available resources, which includes JavaScript files, stylesheets and images directories.

test/ contains all the application tests. Tests are written either as Java JUnit tests or as Selenium tests.

Because Play uses UTF-8 as single encoding, it’s very important that all text files hosted in these directories are encoded using this charset. Make sure to configure your text editor accordingly.

Now if you’re a seasoned Java developer, you may wonder where all the .class files go. The answer is nowhere: Play doesn’t use any class files; instead it reads the Java source files directly. Under the hood we use the Eclipse compiler to compile Java sources on the fly.

That allows two very important things in the development process. The first one is that Play will detect changes you make to any Java source file and automatically reload them at runtime. The second is that when a Java exception occurs, Play will create better error reports showing you the exact source code.

In fact Play keeps a bytecode cache in the application’s tmp/ directory, but only to speed things up between restart on large applications. You can discard this cache using the play clean command if needed.

Running the application
We can now test the newly-created application. Just return to the command line, go to the newly-created yabe/ directory and type play run. Play will now load the application and start a web server on port 9000.

You can see the new application by opening a browser to http://localhost:9000. A new application has a standard welcome page that just tells you that it was successfully created.



Let’s see how the new application can display this page.

The main entry point of your application is the conf/routes file. This file defines all accessible URLs for the application. If you open the generated routes file you will see this first ‘route’:

GET		/			Application.index
That simply tells Play that when the web server receives a GET request for the / path, it must call the Application.index Java method. In this case, Application.index is a shortcut for controllers.Application.index, because the controllers package is implicit.

When you create standalone Java applications you generally use a single entry point defined by a method such as:

public static void main(String[] args) {
  ... 
}
A Play application has several entry points, one for each URL. We call these methods action methods. Action methods are defined in special classes that we call controllers.

Let’s see what the controllers.Application controller looks like. Open the yabe/app/controllers/Application.java source file:

package controllers;
 
import play.mvc.*;
 
public class Application extends Controller {
 
	public static void index() {
		render();
	}
 
}
Notice that controller classes extend the play.mvc.Controller class. This class provides many useful methods for controllers, like the render() method we use in the index action.

The index action is defined as a public static void method. This is how action methods are defined. You can see that action methods are static, because the controller classes are never instantiated. They are marked public to authorize the framework to call them in response to a URL. They always return void.

The default index action is simple: it calls the render() method which tells Play to render a template. Using a template is the most common way (but not the only one) to generate the HTTP response.

Templates are simple text files that live in the /app/views directory. Because we didn’t specify a template, the default one for this action will be used: Application/index.html

To see what the template looks like, open the /yabe/app/views/Application/index.html file:

#{extends 'main.html' /}
#{set title:'Home' /}
 
#{welcome /}
The template content seems pretty light. In fact, all you see are Play tags. Play tags are similar to JSP tags. This is the #{welcome /} tag that generates the welcome message you saw in the browser.

The #{extends /} tag tells Play that this template inherits another template called main.html. Template inheritance is a powerful concept that allows you to create complex web pages by reusing common parts.

Open the /yabe/app/views/main.html template:

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
Do you see the #{doLayout /} tag near the bottom? This is where the content of Application/index.html will be inserted.

We can try to edit the controller file to see how Play automatically reloads it. Open the yabe/app/controllers/Application.java file in a text editor, and add a mistake by removing the trailing semicolon after the render() call:

public static void index() {
    render()
}
Go to the browser and refresh the page. You can see that Play detected the change and tried to reload the Application controller. But because you made a mistake, you get a compilation error.



Ok, let’s correct the error, and make a real modification:

public static void index() {
    System.out.println("Yop");
    render();
}
This time, Play has correctly reloaded the controller and replaced the old code in the JVM. Each request to the / URL will output the ‘Yop’ message to the console.

You can remove this useless line, and now edit the yabe/app/views/Application/index.html template to replace the welcome message:

#{extends 'main.html' /}
#{set title:'Home' /}
 
<h1>A blog will be here</h1>
Like for Java code changes, just refresh the page in the browser to see the modification.

We will now start to code the blog application. You can either continue to work with a text editor or open the project in a Java IDE like Eclipse or NetBeans. If you want to set-up a Java IDE, see Setting-up your preferred IDE.

Setting-up the database
One more thing before starting to code. For the blog engine, we will need a database. For development purposes, Play comes with a stand alone SQL database management system called H2. This is the best way to start a project before switching to a more robust database if needed. You can choose to have either an in-memory database or a filesystem database that will keep your data between application restarts.

At the beginning, we will do a lot of testing and changes in the application model. For that reason, it’s better to use an in-memory database so we always start with a fresh data set.

To set-up the database, open the yabe/conf/application.conf file and uncomment this line:

db=mem
As you can see in the comments, you can easily set-up any JDBC compliant database and even configure the connection pool.

This tutorial is designed to work with the in-memory database; instructions for using JPA with other databases is outside the scope of this tutorial.

Now, go back to your browser and refresh the welcome page. Play will automatically start the database. Check for this line in the application logs:

INFO  ~ Connected to jdbc:h2:mem:play
Using a version control system to track changes
When you work on a project, it’s highly recommended to store your source code in a version control system (VCS). It allows you to revert to a previous version if a change breaks something, work with several people and give access to all the successive versions of the application.

When storing a Play application in a VCS, it is important to exclude the tmp/, modules/, lib/, test-result/ and logs/ directories.

If you are using Eclipse, and the play eclipsify command, then you should also exclude .classpath and eclipse/.

Bazaar
Here we will use Bazaar as an example. Bazaar is a distributed source version control system.

Installing Bazaar is beyond the scope of this tutorial but it is very easy on any system. Once you have a working installation of Bazaar, go to the blog directory and init the application versioning by typing:

$ bzr init
$ bzr ignore tmp
$ bzr ignore modules
$ bzr ignore lib
$ bzr ignore test-result
$ bzr ignore logs
Now we can commit our first blog engine version:

$ bzr add
$ bzr commit -m "YABE initial version"
Git
Git is another distributed version control system, see its documentation for more information.

Create a git working repository at the application root directory:

$ git init
Create a .gitignore file containing the following content:

/tmp
/modules
/lib
/test-result
/logs
Add the content of the application and commit it:

$ git add .
$ git commit -m "YABE initial version"
Version 1 is committed and we now have a solid foundation for our project.

Go to the A first iteration of the data model.
