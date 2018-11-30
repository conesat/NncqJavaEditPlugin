# NncqJavaEditPlugin.md
**NNCQ专用 快速注解生成插件**

#### 使用的注解

>  ApiOperation RequestMapping PreAuthorize

#### 安装方式
 
>  点击下载app中的jar [NncqJavaEditPlugin.jar](https://github.com/conesat/NncqJavaEditPlugin/blob/master/app/)
>  在Idea中选择`File`--->`Settings`--->`Plugins`--->`Install plugin from disk...` 选择下载的插件即可

#### 编写格式

#### @ApiOperation注解
>
> 格式  a b c 三个参数中间空格间隔 a可选[a|api|apioperation]任意一个
>
> 不区分大小写 对应都是ApiOperation b必填,对应value c可选对应notes不填默认取b的值
>
> [a|api|apioperation] AAA BBB 等同于 @ApiOperation(value = "AAA", notes = "BBB") 

#### @RequestMapping注解
>
> 格式  a b c 三个参数中间空格间隔 a可选[r|req|requestmapping]任意一个
>
> 不区分大小写 对应都是RequestMapping b必填,对应value 
>
> c可选对应method不填默认取GET  可使用缩写 g代表GET p代表POST d代表DELETE pu代表PUT 
>
> 当然也可以写完整的或者其他值
>
> [r|req|requestmapping] getList/{id} g 等同于 @ApiOperation(value = "getList/{id}", method = RequestMethod.GET) 
>
> 默认情况下会生成对应的@PreAuthorize注解 
>
> 如果不希望生成则可以在[r|req|requestmapping] getList/{id} g nopre 后加上 nopre则不生成
>


#### 生成快捷键 Alt+Insert 或者鼠标右键

#### NNCQ
