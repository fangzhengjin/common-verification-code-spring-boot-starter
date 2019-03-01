package com.github.fangzhengjin.common.autoconfigure.verification

import com.github.fangzhengjin.common.component.verification.VerificationHelper
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationValidatorProvider
import com.github.fangzhengjin.common.component.verification.service.impl.DefaultVerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.impl.DefaultVerificationValidatorProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.http.HttpServletRequest
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
    @ConditionalOnMissingBean(VerificationGeneratorProvider::class)
    fun defaultVerificationGeneratorProvider(): VerificationGeneratorProvider {
        return DefaultVerificationGeneratorProvider()
    }

    @Bean
    @ConditionalOnMissingBean(VerificationValidatorProvider::class)
    fun defaultVerificationValidateProvider(): VerificationValidatorProvider {
        return DefaultVerificationValidatorProvider()
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(VerificationHelper::class)
    fun verificationHelper(
            request: HttpServletRequest,
            response: HttpServletResponse,
            session: HttpSession,
            verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>,
            verificationValidatorProviders: MutableList<VerificationValidatorProvider>
    ): VerificationHelper {
        return VerificationHelper.init(
                request,
                response,
                session,
                verificationGeneratorProviders,
                verificationValidatorProviders
        )
    }
}