<center>Feosd-后台管理demo</center>

![](https://img.shields.io/badge/language-Java-red.svg) ![](https://img.shields.io/badge/platform-Java-blue.svg)

# 简介

Feosd是一款基于SpringBoot+代码生成器的快速开发平台！采用前后端分离架构：Feosd。强大的代码生成器让前端和后台代码一键生成，不需要写任何代码，绝对是全栈开发福音！！

# 基础环境 

1. java8+
2. 开发框架 springboot2.0.4,spring5,jersey2,swagger2,hibernate-validate4,jpa,hibernate5

# 开发说明

1.查询过滤器
已封装PageQuery分页查询对象，可以实现单表全字段查询，在service-api层定义 Page<T> get...Page(PageQuery query) 接口并在service层实现即可，参考Page<SysUserDto> getSysUserPage(PageQuery query)

2.字典翻译 
已封装@Dict字典翻译注解,在Dto的字段上添加@Dict(dicCode = "sex")即可，返回字段中会增加一个_dicText字段，作为翻译字段。
两类字典翻译
1）通过字典表翻译，如：@Dict(dicCode = "sex")

2）通过关联表翻译，如：@Dict(dicCode = "id",dictTable="sys_depart",dicText="depart_name")

3.Excel导入导出
已封装@Excel注解，在Dto的字段上添加@Excel(name = "性别",replace = {"男_0","女_1"},orderNum = "6")即可。
具体用法参见：https://opensource.afterturn.cn/doc/easypoi.html#40202

4.缓存工具类
已添加缓存操作工具类，根据项目配置，自动选择存到本地内存或redis
@Autowired
DataCache dataCache; 

5.任务管理器
新建一个Java类，继承JpJob，实现其中的runJob方法。

# 注意事项

1.Dto是跟前端进行交互的数据对象，字段说明务必写清楚,比如 @ApiModelProperty(value = "性别 0-男，1-女")
