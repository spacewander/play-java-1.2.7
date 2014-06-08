# 通过CRUD来实现一个基本的管理面板

目前，我们还没法使用博客的UI来写新的文章，或修改评论。Play提供了一个即开即用的**CRUD**模块，可以快速生成一个基本的管理面板。

## 启动CRUD模块

一个Play应用可以由几个模块组装起来。这使得你可以在不同应用间重用组件或分割一个大应用到几个小的。

CRUD模块是一个通用的应用，可以对模型类进行内省生成简单的列表和表单。

要启动CRUD模块，在`/conf/dependencies.yml`的`require`后面添加一行：

	require:
		- play -> crud
		
现在运行`play dependencies`命令，来解决新的模块依赖关系。如果正用着IDE，你应该更新项目配置，来包括新的模块依赖：比如，运行`play eclipsify`，然后在Eclipse里刷新项目。

然后这个模块提供一系列现在就能用上的**路由**。要导入这些路由，在`/yabe/conf/routes`加入：

	# Import CRUD routes
	*      /admin              module:crud
	
这将导入所有的CRUD路由，并以`/admin`作为URL前缀。

你需要重启应用来使得新模块的导入生效。

## 声明CRUD控制器

对于每个想集成到管理面板的模型，我们得声明一个继承自`controllers.CRUD`的控制器。这很简单。

给每个模型创建各创建一个控制器。比如，对于`Post`类，在`/yabe/app/controllers/Posts.java`创建一个`Posts`控制器。

	package controllers;
	 
	import play.*;
	import play.mvc.*;
	 
	public class Posts extends CRUD {    
	}
	
> 默认控制器的命名，是其对应的模型的复数。这样，Play就能自动搭配每个控制器和对应的模型。如果你需要指定特别的名字，你可以使用`@CRUD.For`注解。阅读[CRUD](http://www.playframework.com/documentation/1.2.7/crud)文档。

同样创建其他的控制器：

	package controllers;
	 
	import play.*;
	import play.mvc.*;
	 
	public class Users extends CRUD {    
	}
	package controllers;
	 
	import play.*;
	import play.mvc.*;
	 
	public class Comments extends CRUD {    
	}
	package controllers;
	 
	import play.*;
	import play.mvc.*;
	 
	public class Tags extends CRUD {    
	}
	
现在打开http://localhost:9000/admin/，你应该看到管理面板。

![admin](image/guide7-1.png)

如果仔细看，你将注意到列表中对象的名字有点奇怪。这是因为默认是以`toString()`的输出来得到一个模型对象的表示。

所以，通过提供定制的`toString()`，我们就能解决这个问题。举个例子，对于User类：

	…
	public String toString() {
		return email;
	}
	…
	
## 添加验证

通常使用管理面板的问题是，提交的表单没有经过恰当的验证。但因为CRUD模块可以从验证注解提取出验证规则，所以如果模型类得到正确注解，就不会有问题。

让我们给`User`类添加一些注解。

	package models;
	 
	import java.util.*;
	import javax.persistence.*;
	 
	import play.db.jpa.*;
	import play.data.validation.*;
	 
	@Entity
	public class User extends Model {
	 
		@Email
		@Required
		public String email;
		
		@Required
		public String password;
		
		public String fullname;
		public boolean isAdmin;
	…
	
现在如果你来到`User`模型的编辑或创建表单，你将看到验证规则已经魔法般添加进去了。

![validation](image/guide7-2.png)

接下来是`Post`类：

	package models;
	 
	import java.util.*;
	import javax.persistence.*;
	 
	import play.db.jpa.*;
	import play.data.validation.*;
	 
	@Entity
	public class Post extends Model {
	 
		@Required
		public String title;
		
		@Required
		public Date postedAt;
		
		@Lob
		@Required
		@MaxSize(10000)
		public String content;
		
		@Required
		@ManyToOne
		public User author;
		
		@OneToMany(mappedBy="post", cascade=CascadeType.ALL)
		public List<Comment> comments;
		
		@ManyToMany(cascade=CascadeType.PERSIST)
		public Set<Tag> tags;
	…

然后检查结果：

![post result](image/guide7-3)

这里你会看到一个有趣的副作用：`@MaxSize`验证规则改变了Play显示Post表单的方式。现在它给内容域准备的是textarea。

最后是给`Comment`和`Tag`类添加验证规则。

	package models;
	 
	import java.util.*;
	import javax.persistence.*;
	 
	import play.db.jpa.*;
	import play.data.validation.*;
	 
	@Entity
	public class Tag extends Model implements Comparable<Tag> {
	 
		@Required
		public String name;
	…
	package models;
	 
	import java.util.*;
	import javax.persistence.*;
	 
	import play.db.jpa.*;
	import play.data.validation.*;
	 
	@Entity
	public class Comment extends Model {
	 
		@Required
		public String author;
		
		@Required
		public Date postedAt;
		 
		@Lob
		@Required
		@MaxSize(10000)
		public String content;
		
		@ManyToOne
		@Required
		public Post post; 
	…


