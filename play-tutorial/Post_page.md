# 浏览和提交评论

博客主页现在已经完成，接下来要完成博客正文页面。整个页面将展示当前文章的所有评论，还包括一个用于提交新的评论的表单。

## 创建'show' action

要显示文章内容，我们需要在`Application`控制器添加新的action。就叫它`show()`：

    public static void show(Long id) {
        Post post = Post.findById(id);
        render(post);
    }

如你所见，整个action简明扼要。我们接受一个id参数作为Long类型Java对象。而这个参数可以来自于URL路径或HTTP请求正文。

> 如果接收到的id参数不是有效的数字，`id`的值会是`null`，而Play会在`errors`容器中新增一个验证错误。

这个action会显示`/yabe/app/views/Application/show.html`模板：

    #{extends 'main.html' /}
    #{set title:post.title /}
     
    #{display post:post, as:'full' /}
    
因为之前写好了`display`标签，写这个页面就变得简单。

## 给正文页面添加链接

在display标签中，我们让所有的链接保持为空（使用`#`）。是时候让这些链接指向`Application.show` action。在Play模板中，你可以简单地用`@{...}`记号来创建链接。这个语法使用路由来“转换”URL成对应的action。

修改`/yabe/app/views/tags/display.html`标签：

    …
    <h2 class="post-title">
        <a href="@{Application.show(_post.id)}">${_post.title}</a>
    </h2>
    …
    
现在刷新主页，点击一个标题来展示正文。

呃……好像缺了个返回主页面的链接。修改`/yabe/app/views/main.html`模板来完成标题链接：

    …
    <div id="title">
        <span class="about">About this blog</span>
        <h1><a href="@{Application.index()}">${blogTitle}</a></h1>
        <h2>${blogBaseline}</h2>
    </div>
    …

现在终于可以在主页和正文之间浏览了。

## 指定一个更语义化的URL

如你所见，正文页面的URL是：

    /application/show?id=1
   
这是因为Play的默认路由规则就是这样：

    *       /{controller}/{action}                  {controller}.{action}
    
通过指定`Application.show` action的路径，我们可以使用更语义化的URL。修改`/yabe/conf/routes`并在第一个路由下面添加新的路由：

    GET     /posts/{id}                             Application.show
    
> 这里`id`参数将从URL路径提取。你可以从[Route File Syntax](http://www.playframework.com/documentation/1.2.7/routes#syntax)中阅读更多关于URI模式的内容。

刷新浏览器，检查这次是否使用了正确的URL。

## 添加分页

要允许用户在文章间方便地流连忘返，我们需要添加分页机制。我们将拓展Post类来按需获取上一篇和下一篇文章：

    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
这个方法在每次请求时都会被多次调用，所以可以优化它们，不过现在先搁置。同时，在`show.html`模板顶部（在`#{display/}`标签前）添加分页链接：

    <ul id="pagination">
        #{if post.previous()}
            <li id="previous">
                <a href="@{Application.show(post.previous().id)}">
                    ${post.previous().title}
                </a>
            </li>
        #{/if}
        #{if post.next()}
            <li id="next">
                <a href="@{Application.show(post.next().id)}">
                    ${post.next().title}
                </a>
            </li>
        #{/if}
    </ul>
    
现在是不是更棒了？


