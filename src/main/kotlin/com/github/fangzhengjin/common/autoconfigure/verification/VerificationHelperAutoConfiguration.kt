package com.github.fangzhengjin.common.autoconfigure.verification

import com.github.fangzhengjin.common.component.verification.VerificationHelperWithRedis
import com.github.fangzhengjin.common.component.verification.VerificationHelperWithSession
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationValidatorWithSessionProvider
import com.github.fangzhengjin.common.component.verification.service.impl.generator.DefaultImageVerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.impl.generator.DefaultMailVerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.impl.validator.DefaultImageVerificationValidatorProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
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

    /**
     * 图形验证码
     */
    @Bean
    @ConditionalOnMissingBean(DefaultImageVerificationGeneratorProvider::class)
    fun defaultImageVerificationGeneratorProvider(): VerificationGeneratorProvider {
        return DefaultImageVerificationGeneratorProvider()
    }

    /**
     * 数字验证码
     */
    @Bean
    @ConditionalOnMissingBean(DefaultMailVerificationGeneratorProvider::class)
    fun defaultMailVerificationGeneratorProvider(): VerificationGeneratorProvider {
        return DefaultMailVerificationGeneratorProvider()
    }

    @Bean
    @ConditionalOnMissingBean(VerificationValidatorWithSessionProvider::class)
    fun defaultVerificationValidateProvider(): VerificationValidatorWithSessionProvider {
        return DefaultImageVerificationValidatorProvider()
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(VerificationHelperWithSession::class)
    fun verificationHelper(
            request: HttpServletRequest,
            response: HttpServletResponse,
            session: HttpSession,
            verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>,
            verificationValidatorProviders: MutableList<VerificationValidatorWithSessionProvider>
    ): VerificationHelperWithSession {
        return VerificationHelperWithSession(
                request,
                response,
                session,
                verificationGeneratorProviders,
                verificationValidatorProviders
        )
    }

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnBean(StringRedisTemplate::class)
    @ConditionalOnMissingBean(VerificationHelperWithRedis::class)
    fun verificationHelper(
            response: HttpServletResponse,
            redisTemplate: StringRedisTemplate,
            verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>
    ): VerificationHelperWithRedis {
        return VerificationHelperWithRedis(
                response,
                redisTemplate,
                verificationGeneratorProviders
        )
    }
}