# common-verification-code-spring-boot-starter

[![Codecov branch](https://img.shields.io/codecov/c/github/fangzhengjin/common-verification-code-spring-boot-starter/master.svg?logo=codecov&style=flat-square)](https://codecov.io/gh/fangzhengjin/common-verification-code-spring-boot-starter)
[![Build Status](https://img.shields.io/travis/com/fangzhengjin/common-verification-code-spring-boot-starter/master.svg?style=flat-square)](https://travis-ci.com/fangzhengjin/common-verification-code-spring-boot-starter)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.fangzhengjin/common-verification-code-spring-boot-starter.svg?style=flat-square&color=brightgreen)](https://maven-badges.herokuapp.com/maven-central/com.github.fangzhengjin/common-verification-code-spring-boot-starter/)
[![Bintray](https://img.shields.io/bintray/v/fangzhengjin/maven/common-verification-code-spring-boot-starter.svg?style=flat-square&color=blue)](https://bintray.com/fangzhengjin/maven/common-verification-code-spring-boot-starter/_latestVersion)
[![License](https://img.shields.io/github/license/fangzhengjin/common-verification-code-spring-boot-starter.svg?style=flat-square&color=blue)](https://www.gnu.org/licenses/gpl-3.0.txt)
[![SpringBootVersion](https://img.shields.io/badge/SpringBoot-2.1.3-heightgreen.svg?style=flat-square)](https://spring.io/projects/spring-boot)

## 导入依赖
```groovy
dependencies {
    implementation "com.github.fangzhengjin:common-verification-code-spring-boot-starter:version"
}
```

## 开启需要的组件
```yaml
#以下选项默认均为关闭
customize: 
  common: 
    verification: 
      recaptcha: 
        enable: true
      redis:
        enable: true
      session:
        enable: true
```

## 使用VerificationHelper
1. 如需实现自定义验证码生成器，请参考下方样例(提供默认实现，不可更改默认实现配置)
```kotlin
/**
 * 实现VerificationGeneratorProvider接口下的render方法和isSupports并将当前实现类注册到Spring容器中
 */
@Component
class DefaultVerificationGeneratorProvider : VerificationGeneratorProvider {
    /**
      * 验证码生成
      */
    override fun render(): VerificationCode {
        return VerificationCode(
                randomCode, //验证码文本 
                buffImg     //验证码图片流
            )
    }
    /**
     * 是否支持该类型的验证码生成
     */
    override fun isSupports(verificationType: VerificationType): Boolean {
        return verificationType == VerificationType.IMAGE
    }
}
```

2. 如需实现自定义验证码验证器，请实现VerificationValidatorProvider接口下的render方法和isSupports并注入到Spring容器中(参考自定义验证码生成)

3. 使用VerificationHelper对象
```kotlin
@RestController
class DemoController {

    @GetMapping("/verify/image")
    fun image() {
        val render = VerificationHelper.render()
        println("验证码：$render")
    }

    @GetMapping("/verify/check")
    fun verifyCheck(@RequestParam code: String) {
        /**
         * 验证码校验
         * @param code                              用户输入的验证码
         * @param expireInSeconds                   验证码有效期(秒),默认60
         * @param cleanupVerificationInfoWhenWrong  验证码输入错误时,是否作废之前的验证码信息,默认false
         * @param throwException                    验证不通过时,是否抛出异常,默认false
         * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
         */
        val status = VerificationHelper.validate(
                code, //用户输入的验证码
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
