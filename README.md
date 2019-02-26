# common-verification-code-spring-boot-starter

[ ![Download](https://api.bintray.com/packages/fangzhengjin/maven/common-verification-code-spring-boot-starter/images/download.svg) ](https://bintray.com/fangzhengjin/maven/common-verification-code-spring-boot-starter/_latestVersion)

## 导入依赖
```groovy
repositories {
    maven { url "https://dl.bintray.com/fangzhengjin/maven/" }
}

dependencies {
    implementation "com.github.fangzhengjin:common-verification-code-spring-boot-starter:version"
}
```

## 两步使用VerificationHelper
1. 实现自定义验证码生成器并配置(提供默认实现，不可更改默认实现配置)
```kotlin
/**
 * 实现VerificationProvider接口下的render方法并将当前实现类注册到Spring容器中
 */
@Component
class DefaultVerificationProvider : VerificationProvider {
    override fun render(): VerificationCode {
        return VerificationCode(
                randomCode, //验证码文本 
                buffImg     //验证码图片流
            )
    }
}
```
2. 在需要使用的地方注入VerificationHelper对象
```kotlin
@RestController
class DemoController {
    @Autowired
    lateinit var verificationHelper: VerificationHelper

    @GetMapping("/verify/image")
    fun image() {
        val render = verificationHelper.render()
        println("验证码：$render")
    }

    @GetMapping("/verify/check")
    fun verifyCheck() {
        val status = verificationHelper.validate(
                "1234", //用户输入的验证码
                60L     //验证码有效期(秒)，默认60秒
        )
        val msg = when (status) {
            VerificationStatus.SUCCESS -> VerificationStatus.SUCCESS.message
            VerificationStatus.WRONG -> "验证码错误"
            VerificationStatus.NOT_FOUNT -> "认证信息未找到"
            VerificationStatus.EXPIRED -> "验证码已过期"
        }
    }
}
```