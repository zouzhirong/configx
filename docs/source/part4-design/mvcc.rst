配置的多版本控制
--------------------


在自动发布模式下，更改任何一个配置，都会立刻自动发布。
在审核发布模式下，有可能修改了多个配置，然后一起发布。

客户端默认情况下，每次访问配置相关的属性或者配置Bean时，都会获取最新的配置。
例如：
```
List<Student> studentList1 = students.getStudents();
List<Student> studentList2 = students.getStudents();
```
假如执行完第一行代码之后，得到了studentList1的值，然后更新到了students配置文件的内容，那么studentList2将会得到最新的值。

这在大部分情况下是正确的，但是假如配置是一个跟金钱相关的，比如买道具需要的钱的数量，如果同一次请求中，两次获取到的钱的数量不一致，就可能导致问题。

为了保证在一个“事务”中，获取到的配置都是一致的，我们在configx-client中增加了多版本控制（mvcc）支持，即在configx-client中会保存配置的多个版本，而“事务”的整个生命周期中看到的只是配置的一个版本。
还是以上面的例子为例：
```
List<Student> studentList1 = students.getStudents();
List<Student> studentList2 = students.getStudents();
```
假如执行第一行代码时，students的配置版本为1，接着更新到了students配置文件的内容，配置版本为2，configx-client中同时有两个版本的students，由于在第一行代码执行时，获取到的配置版本为1，所以执行第二行代码时，获取到的配置版本还是1，配置版本为2的更新需要等待下一次“事务”才会生效。

多版本控制为了保证同一个“事务”中，不管配置是否被修改，这个事务中看到的配置是一致的。
开启多版本控制，需要在configx.properties中添加属性：
```
configx.client.mvcc.enabled=true
```
开启了mvcc之后，需要在程序中手动清除线程的版本号信息，否则线程将一直使用第一次的版本号，不会随着配置的更新而自动更新。
清除线程中的版本号，调用以下方法：
```
ConfigVersionManager.clearCurrentVersion();
```

通常在一个“事务”结束以后，需要清理线程中的版本号。
常见的“事务”比如：
1、一个Http请求，可以在Filter中清除，比如：
```
@Override
public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException
{
    try {
        ...
    } catch (Exception e) {
        ...
    } finally {
        ConfigVersionManager.clearCurrentVersion();
    }
}
```

2、在线程池中执行的任务，可以在ThreadPoolExecutor的afterExecute方法中清除，比如：
```
@Override
protected void afterExecute(Runnable r, Throwable t) {
    ConfigVersionManager.clearCurrentVersion();
    super.afterExecute(r, t);
}
```

如果是Spring TaskExecutor，可以使用ConcurrentTaskExecutor来自定义ThreadPoolExecutor，覆盖afterExecute方法。
      Spring TaskScheduler，可以覆盖ConcurrentTaskScheduler来自定义ThreadPoolExecutor，覆盖afterExecute方法。

详细请参考：

[Spring Task Execution and Scheduling](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling)

[TaskExecutor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)

[ThreadPoolTaskExecutor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html)

[ConcurrentTaskExecutor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ConcurrentTaskExecutor.html)

[TaskScheduler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/TaskScheduler.html)


[ThreadPoolTaskScheduler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskScheduler.html)

[ConcurrentTaskScheduler](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ConcurrentTaskScheduler.html)






3、自定义“事务”，可以使用try...finally来清除，比如：
```
try {
    ...
} finally {
    ConfigVersionManager.clearCurrentVersion();
}
```
