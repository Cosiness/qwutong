qwutong
=======

梧桐不死，只是高飞。

梧桐2014预览版
1. 公司首页拆分
a. 所有子页面分离，在reside menu里有独立入口，导航结构更扁平，单个页面浏览性能更优。
b. 左侧导航菜单替换为新酷的residing menu，效果见视频：http://v.youku.com/v_show/id_XNzQ1NjcxMDE2.html?x 
2. 暂时舍弃功能：
导航只在公司圈子内进行，其他部分后续恢复。
右侧导航未充分设计定义。
3. 实现代码
a. 更激进的破耦合策略
b. 导航，航拍特效，保持独立的小库集成，数据库，异步访问，自定义标题头在类继承体系内进行污染隔离与阻断，让业务代码，辅助性结构代码严格限定在类范围里。
源代码可访问github: https://github.com/Cosiness/qwutong
（切换到dev-reside-menu分支）
