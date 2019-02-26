package com.github.fangzhengjin.common.autoconfigure.verification

import com.github.fangzhengjin.common.component.verification.VerificationHelper
import com.github.fangzhengjin.common.component.verification.service.VerificationProvider
import com.github.fangzhengjin.common.component.verification.service.impl.DefaultVerificationProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * @version V1.0
 * @title: VerificationHelperAutoConfiguration
 * @package com.github.fangzhengjin.common.autoconfigure.verification
 * @description: 注册验证码助手
 * @author fangzhengjin
 * @date 2019/2/26 16:59
 */
@Configuration
class VerificationHelperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(VerificationProvider::class)
    fun defaultVerificationProvider(): VerificationProvider {
        return DefaultVerificationProvider()
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(VerificationHelper::class)
    fun verificationHelper(
            response: HttpServletResponse,
            session: HttpSession,
            verificationProvider: VerificationProvider
    ): VerificationHelper {
        return VerificationHelper(response, session, verificationProvider)
    }
}