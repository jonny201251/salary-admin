druid访问地址
    http://localhost/druid/login.html
    用户名和密码：admin和hthyaq
swagger访问地址：http://localhost/swagger-ui.html
ureport访问地址：http://localhost/ureport/designer

管理员：admin/admin
登录密码：身份证号后6位、123

1.应发奖金或过节费表，修改为-其他薪金
2.离退休工资-sal_ltx，仅仅是退休，不包括离休
3.变动单
  人员管理
  	新增人员
  	人员的部门调动
  	内聘人员-离职
  	内聘人员-死亡
  	内聘人员-调出
  	内聘人员-退休
  	内聘人员-离休
  	退休人员-死亡
  	离休人员-死亡
  部门管理
  	部门名称变更
  	部门重组
  
  线上的域名和文件路径：
      haiyingmall.paas.casicloud.com
      /usr/local/tomcat/webapps/salaryFile
      
  部署
    修改yml的数据库
    修改util/Constants中的D盘，还是E盘
    修改logback-spring.xml的配置
    修改ureport.properties中ureport.debug=false