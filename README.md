# ScrollText
做了一个红包的翻倍效果，动画逻辑都比较简单，但是轮子也是现成的，所以打算开源给大家用一下，如果有类似效果的兄弟可以直接用。同时如果有其他效果也可以看源码自己改 代码只有一个类。

支持常规TextView的属性，具体使用方法可以参考例子



使用方式：

```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}


dependencies {
	        implementation 'com.github.a369155609:ScrollText:v0.0.4'
}
```



```Kotlin
val rollingView = findViewById<TextScrollView>(R.id.rollText)
rollingView.setNum("2.36","7.62")
```

![示例图片](https://a369155609.github.io/picx-images-hosting/ezgif-3-a67b249123.5c0xguo3wm.gif)
