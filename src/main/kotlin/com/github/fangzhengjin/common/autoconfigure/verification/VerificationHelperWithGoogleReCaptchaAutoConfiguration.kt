package com.github.fangzhengjin.common.autoconfigure.verification

import com.github.fangzhengjin.common.component.verification.VerificationHelperWithGoogleReCaptcha
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @version V1.0
 * @title: VerificationHelperWithSessionAutoConfiguration
 * @package com.github.fangzhengjin.common.autoconfigure.verification
 * @description: 验证码助手GoogleReCaptcha
 * @author fangzhengjin
 * @date 2019/2/26 16:59
 */
@Configuration
@EnableConfigurationProperties(ReCaptchaProperties::class)
@ConditionalOnExpression("\${customize.common.verification.recaptcha.enable:true}")
class VerificationHelperWithGoogleReCaptchaAutoConfiguration {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(VerificationHelperWithGoogleReCaptcha::class)
    fun verificationHelperWithGoogleReCaptcha(
            reCaptchaProperties: ReCaptchaProperties
    ): VerificationHelperWithGoogleReCaptcha {
        return VerificationHelperWithGoogleReCaptcha(reCaptchaProperties)
    }
}