# 建立第一个页面

既然我们完成了数据模型的初步定义，是时候开始创建应用的页面了。这个页面将仅仅展示最近的博文，以及一个旧文章的列表。

下面是我们想要实现的目标的草图：

![草图](image/guide-mock1.png)

## 用默认数据启动

事实上，在开始编写第一个页面之前，我们还有一件事要做。编写一个缺乏数据的Web应用是乏味的。你甚至不能测试正在做的事情。但因为我们还没完成编辑页面，所以不能发布新的文章作为测试。

有一个注入默认数据到博客中的方法是在应用加载时加载一个fixture文件。要想这么做，我们得创建一个启动（Bootstrap）任务。一个Play任务可以在任意HTTP请求之外执行，比如在应用启动时或者通过CRON，在特定时间点执行。

让我们来创建`/yabe/app/Bootstrap.java`任务，通过`Fixtures`加载一堆数据：

    import play.*;
    import play.jobs.*;
    import play.test.*;
     
    import models.*;
     
    @OnApplicationStart
    public class Bootstrap extends Job {
     
        public void doJob() {
            // Check if the database is empty
            if(User.count() == 0) {
                Fixtures.loadModels("initial-data.yml");
            }
        }
     
    }
    
我们用`@OnApplicationStart`注解这个任务，告诉Play我们希望在应用启动时，同步执行该任务。

> 事实上该任务的启动时机取决于是在开发模式还是在生产模式。在开发模式，Play会等待第一个请求才开始。所以任务会在第一个请求时同步执行。这样，当任务失败，你将在浏览器中看到错误信息。在生产模式，任务会在应用启动时执行（就在运行`play run`的时候），如果出错，应用将无法启动。

你可以在`yabe/conf/`文件夹下创建一个`initial-data.yml`。你当然可以重用我们之前用过的`data.yml`文件。

现在用`play run`运行应用，并在浏览器打开http://localhost:9000/

## 主页面

终于，是时候编写主页了。

你还记得第一个页面是如何输出的么？首先路由文件指定`/`URL将调用`controllers.Application.index()`action方法。然后这个方法调用`render()`并执行`/yabe/app/views/Application/index.html`模板。

我们将保持这些组件，不过给它们添加新的代码来加载文章列表并展示。

打开`/yabe/app/views/Application/index.html`控制器，修改`index()`action来加载文章列表，就像这样：

    package controllers;
     
    import java.util.*;
     
    import play.*;
    import play.mvc.*;
     
    import models.*;
     
    public class Application extends Controller {
     
        public static void index() {
            Post frontPost = Post.find("order by postedAt desc").first();
            List<Post> olderPosts = Post.find(
                "order by postedAt desc"
            ).from(1).fetch(10);
            render(frontPost, olderPosts);
        }
     
    }
    
你可以看懂我们是怎样向`render`方法传递对象的吗？这将允许我们用同样的名字在模板中访问它们。在这个例子，变量`frontPage`和`olderPosts`将在模板中可用。

修改`/yabe/app/views/Application/index.html`来展示这些对象：

    #{extends 'main.html' /}
    #{set title:'Home' /}
     
    #{if frontPost}
        <div class="post">
            <h2 class="post-title">
                <a href="#">${frontPost.title}</a>
            </h2>
            <div class="post-metadata">
                <span class="post-author">by ${frontPost.author.fullname}</span>
                <span class="post-date">${frontPost.postedAt.format('MMM dd')}</span>
                <span class="post-comments">
                    &nbsp;|&nbsp; 
                    ${frontPost.comments.size() ?: 'no'} 
                    comment${frontPost.comments.size().pluralize()}
                    #{if frontPost.comments}
                        , latest by ${frontPost.comments[-1].author}
                    #{/if}
                </span>
            </div>
            <div class="post-content">
                ${frontPost.content.nl2br()}
            </div>
        </div>
        
        #{if olderPosts}
            <div class="older-posts">    
                <h3>Older posts <span class="from">from this blog</span></h3>
            
                #{list items:olderPosts, as:'oldPost'}
                    <div class="post">
                        <h2 class="post-title">
                            <a href="#">${oldPost.title}</a>
                        </h2>
                        <div class="post-metadata">
                            <span class="post-author">
                                by ${oldPost.author.fullname}
                            </span>
                            <span class="post-date">
                                ${oldPost.postedAt.format('dd MMM yy')}
                            </span>
                            <div class="post-comments">
                                ${oldPost.comments.size() ?: 'no'} 
                                comment${oldPost.comments.size().pluralize()}
                                #{if oldPost.comments}
                                    - latest by ${oldPost.comments[-1].author}
                                #{/if}
                            </div>
                        </div>
                    </div>
                #{/list}
            </div>
            
        #{/if}
        
    #{/if}
     
    #{else}
        <div class="empty">
            There is currently nothing to read here.
        </div>
    #{/else}
    
你可以在阅读模板是怎么工作的。简单地说，它允许你动态访问Java对象。在幕后我们使用Groovy的语法。大多数你看到的优雅的结构（比如`?:`运算符）就来自Groovy。但你并不需要为了写Play模板而学习Groovy。如果已经熟悉其他像JSP with JSTL的模板语言，你不会感到惘然无所适。

Ok，现在刷新博客的首页。
