备注：第一运行需要重新编译，否则不能运行

1.项目名称：NetMusicControl


2.运行环境：Eclipse SDK 3.5.2 + Android SDK


3.在配置好eclipse+android的开发环境后，把此项目导入即可


4.项目目录结构：

NetMusicControl
	--asssets		暂时没有用到（是存放音频等资源的）
	--bin			项目输出文件夹
	--gen			Android项目R.java文件所在目录
	--res			Android项目资源文件夹
	  --drawable	图片资源文件夹
	  --layout	布局资源文件夹
	  --values	字串变量资源文件夹

	--src			主目录
	  --com		包名
	    --sungeo（包）
	      --connet（包）			//存放socket连接相关类以及与webservice通讯相关类
	      --data（包）			//存放歌曲、专辑数据相关类
	      --download（包）			//存放实现多线程下载的相关类
	      --infomanager（包）		//存放包括专辑、下载、录音管理类
	      --objects（包）			//存放与网关通讯包的封装类
	      --protocl（包）			//存放与协议相关类
	      --unit（包）			//存放与UI相关的控件类
	      --BootReceiver.java		//实现开机启动的类
	      --RecordManagerActivity.java      //录音界面
	      --RootActivity.java		//所有界面的父类
	      --ServiceMain.java		//服务类，为实现唤醒后播歌功能
	      --SetSerialActivity.java		//设置序列号界面
	      --SongListActivity.java		//歌曲列表界面
	      --SungeoEntry.java		//程序主入口

	--.classpath		eclipse项目配置文件
	--.project		eclipse项目配置文件
	--AndroidManifest.xml	android项目配置文件
	--default.properties	eclipse项目配置文件
	--proguard.cfg		eclipse项目配置文件


