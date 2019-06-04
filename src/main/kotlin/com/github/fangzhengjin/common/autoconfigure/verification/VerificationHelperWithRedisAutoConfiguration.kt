package com.github.fangzhengjin.common.autoconfigure.verification

import com.github.fangzhengjin.common.component.verification.VerificationHelperWithRedis
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import javax.servlet.http.HttpServletResponse

/**
 * @version V1.0
 * @title: VerificationHelperWithRedisAutoConfiguration
 * @package com.github.fangzhengjin.common.autoconfigure.verification
 * @description: 注册验证码助手
 * @author fangzhengjin
 * @date 2019/2/26 16:59
 */

@Configuration
@AutoConfigureAfter(name = ["org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"])
class VerificationHelperWithRedisAutoConfiguration {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnBean(StringRedisTemplate::class)
    @ConditionalOnMissingBean(VerificationHelperWithRedis::class)
    fun verificationHelperWithRedis(
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