package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.common.component.verification.exception.*
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import com.github.fangzhengjin.common.component.verification.service.VerificationType
import com.github.fangzhengjin.common.component.verification.service.VerificationValidatorProvider
import com.github.fangzhengjin.common.component.verification.vo.VerificationValidateData
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * @version V1.0
 * @title: VerificationHelper
 * @package com.github.fangzhengjin.common.component.verification
 * @description: 验证码助手
 * @author fangzhengjin
 * @date 2019/2/26 16:34
 */
object VerificationHelper {
    @JvmStatic
    private lateinit var request: HttpServletRequest
    @JvmStatic
    private lateinit var response: HttpServletResponse
    @JvmStatic
    private lateinit var session: HttpSession
    @JvmStatic
    private lateinit var verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>
    @JvmStatic
    private lateinit var verificationValidatorProviders: MutableList<VerificationValidatorProvider>

    const val VERIFICATION_CODE_SESSION_KEY = "VERIFICATION_CODE_SESSION_KEY"
    const val VERIFICATION_CODE_SESSION_DATE = "VERIFICATION_CODE_SESSION_DATE"
    const val VERIFICATION_CODE_SESSION_TYPE = "VERIFICATION_CODE_SESSION_TYPE"

    @JvmStatic
    fun init(
            request: HttpServletRequest,
            response: HttpServletResponse,
            session: HttpSession,
            verificationGeneratorProviders: MutableList<VerificationGeneratorProvider>,
            verificationValidatorProviders: MutableList<VerificationValidatorProvider>
    ): VerificationHelper {
        this.request = request
        this.response = response
        this.session = session
        this.verificationGeneratorProviders = verificationGeneratorProviders
        this.verificationValidatorProviders = verificationValidatorProviders
        return this
    }

    /**
     * 生成验证码
     */
    @Throws(VerificationNotFountExpectedGeneratorProviderException::class)
    @JvmStatic
    fun render(verificationType: VerificationType = VerificationType.IMAGE): String {
        verificationGeneratorProviders.forEach {
            if (it.isSupports(verificationType)) {
                val verificationCode = it.render()
                session.setAttribute(VERIFICATION_CODE_SESSION_KEY, verificationCode.code)
                session.setAttribute(VERIFICATION_CODE_SESSION_DATE, LocalDateTime.now())
                session.setAttribute(VERIFICATION_CODE_SESSION_TYPE, verificationCode.verificationType)

                // 只有图片验证码才执行流输出
                if (verificationCode.verificationType == VerificationType.IMAGE) {
                    response.setDateHeader(HttpHeaders.EXPIRES, 0L)
                    response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                    response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0")
                    response.setHeader(HttpHeaders.PRAGMA, "no-cache")
                    response.contentType = MediaType.IMAGE_JPEG_VALUE
                    response.outputStream.use {
                        ImageIO.write(verificationCode.image, "JPEG", it)
                    }
                }

                return verificationCode.code
            }
        }
        throw VerificationNotFountExpectedGeneratorProviderException()
    }

    /**
     * 验证码校验
     * @param code                              用户输入的验证码
     * @param expireInSeconds                   验证码有效期(秒),默认60
     * @param cleanupVerificationInfoWhenWrong  验证码输入错误时,是否作废之前的验证码信息,默认false
     * @param throwException                    验证不通过时,是否抛出异常,默认false
     * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
     */
    @JvmStatic
    @JvmOverloads
    @Throws(
            VerificationNotFountException::class,
            VerificationWrongException::class,
            VerificationExpiredException::class,
            VerificationNotFountExpectedValidatorProviderException::class
    )
    fun validate(
            code: String,
            expireInSeconds: Long = 60,
            cleanupVerificationInfoWhenWrong: Boolean = false,
            throwException: Boolean = false
    ): VerificationStatus {
        val sessionCode = (session.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_KEY)
                ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as String
        val codeCreatedTime = (session.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_DATE)
                ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as LocalDateTime
        val verificationType = (session.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_TYPE)
                ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as VerificationType

        verificationValidatorProviders.forEach {
            if (it.isSupports(verificationType)) {
                return it.render(
                        request,
                        response,
                        session,
                        VerificationValidateData(
                                sessionCode,
                                code,
                                verificationType,
                                LocalDateTime.now().isAfter(codeCreatedTime.plusSeconds(expireInSeconds)),
                                cleanupVerificationInfoWhenWrong,
                                throwException
                        )
                )
            }
        }
        throw VerificationNotFountExpectedValidatorProviderException()
    }
}