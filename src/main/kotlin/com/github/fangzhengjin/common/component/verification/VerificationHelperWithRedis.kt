package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.common.component.verification.exception.*
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import com.github.fangzhengjin.common.component.verification.service.VerificationType
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.StringUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse

/**
 * @version V1.0
 * @title: VerificationHelperWithSession
 * @package com.github.fangzhengjin.common.component.verification
 * @description: 验证码助手
 * @author fangzhengjin
 * @date 2019/2/26 16:34
 */
class VerificationHelperWithRedis(
        private val response: HttpServletResponse,
        private val redisTemplate: StringRedisTemplate,
        private val verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>
) {

    companion object {
        @JvmStatic
        private val sdf = SimpleDateFormat("yyyy-MM-dd")

        const val VERIFICATION_CODE = "VERIFICATION_CODE"
        const val VERIFICATION_CODE_LIMIT = "VERIFICATION_CODE_LIMIT"
    }


    /**
     * 生成验证码
     * @param verificationType                  验证码类型
     * @param codeId                            验证码标识
     * @param limitSecondTime                   刷新间隔时间(秒) 0 无限制
     * @param limitSize                         每日最大上限次数 0 无限制
     * @param expireSecondTime                  过期时间
     */
    @Throws(VerificationNotFountExpectedGeneratorProviderException::class)
    @JvmOverloads
    fun render(
            verificationType: VerificationType = VerificationType.MAIL,
            codeId: String,
            limitSecondTime: Int = 30,
            limitSize: Int = 10,
            expireSecondTime: Long = 60L * 10
    ): String {
        verificationGeneratorProviders.forEach {
            if (it.isSupports(verificationType)) {
                val limitOps = redisTemplate.boundValueOps("$VERIFICATION_CODE_LIMIT:${sdf.format(Date())}:$codeId")
                if (limitSize > 0) {
                    if (limitOps.get() == null) {
                        limitOps.set("0", 1, TimeUnit.DAYS)
                    }
                    if (limitOps.get()!!.toInt() >= limitSize) {
                        throw VerificationUpperLimitException("您当日操作已达上限,请明日再试!")
                    }
                }

                val codeOperations = redisTemplate.boundValueOps("$VERIFICATION_CODE:$codeId")
                if (limitSecondTime != 0) {
                    val expire = codeOperations.expire ?: 0
                    if (!StringUtils.isEmpty(codeOperations.get()) && expireSecondTime - expire < limitSecondTime) {
                        throw VerificationFrequentOperationException("您的请求过于频繁,请稍后重试!")
                    }
                }

                val verificationCode = it.render()
                codeOperations.set(verificationCode.code, expireSecondTime, TimeUnit.SECONDS)

                if (limitSize > 0) limitOps.increment(1)

                // 只有图片数据执行流输出
                if (verificationCode.image != null) {
                    response.setDateHeader(HttpHeaders.EXPIRES, 0L)
                    response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                    response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0")
                    response.setHeader(HttpHeaders.PRAGMA, "no-cache")
                    response.contentType = MediaType.IMAGE_JPEG_VALUE
                    response.outputStream.use { os ->
                        ImageIO.write(verificationCode.image, "JPEG", os)
                    }
                }

                return verificationCode.code
            }
        }
        throw VerificationNotFountExpectedGeneratorProviderException()
    }

    /**
     * 验证码校验
     * @param codeId                            验证码标识
     * @param userInputCode                     用户输入的验证码
     * @param throwException                    验证不通过时,是否抛出异常,默认false
     * @param deleteOnSuccess                   验证通过后是否作废/删除验证码
     * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
     */
    @JvmOverloads
    @Throws(
            VerificationNotFountException::class,
            VerificationWrongException::class,
            VerificationExpiredException::class,
            VerificationNotFountExpectedValidatorProviderException::class
    )
    fun validate(
            codeId: String,
            userInputCode: String,
            throwException: Boolean = false,
            deleteOnSuccess: Boolean = true
    ): VerificationStatus {
        val codeKey = "$VERIFICATION_CODE:$codeId"
        val redisOperations = redisTemplate.opsForValue()
        val redisCode = (redisOperations.get(codeKey)
                ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT)

        return when {
            redisCode.equals(userInputCode, ignoreCase = true) -> {
                if (deleteOnSuccess) redisOperations.operations.delete(codeKey)
                return VerificationStatus.SUCCESS
            }
            throwException -> throw VerificationWrongException()
            else -> VerificationStatus.WRONG
        }
    }
}