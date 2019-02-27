package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.common.component.verification.exception.VerificationExpiredException
import com.github.fangzhengjin.common.component.verification.exception.VerificationNotFountException
import com.github.fangzhengjin.common.component.verification.exception.VerificationWrongException
import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import com.github.fangzhengjin.common.component.verification.service.VerificationType
import com.github.fangzhengjin.common.component.verification.service.VerificationValidateProvider
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
class VerificationHelper(
        requestParam: HttpServletRequest,
        responseParam: HttpServletResponse,
        sessionParam: HttpSession,
        verificationGeneratorProviderParam: VerificationGeneratorProvider,
        verificationValidateProviderParam: VerificationValidateProvider
) {
    init {
        request = requestParam
        response = responseParam
        session = sessionParam
        verificationGeneratorProvider = verificationGeneratorProviderParam
        verificationValidateProvider = verificationValidateProviderParam
    }

    companion object {
        //        private val logger = LoggerFactory.getLogger(this::class.java)
        private var request: HttpServletRequest? = null
        private var response: HttpServletResponse? = null
        private var session: HttpSession? = null
        private var verificationGeneratorProvider: VerificationGeneratorProvider? = null
        private var verificationValidateProvider: VerificationValidateProvider? = null

        const val VERIFICATION_CODE_SESSION_KEY = "VERIFICATION_CODE_SESSION_KEY"
        const val VERIFICATION_CODE_SESSION_DATE = "VERIFICATION_CODE_SESSION_DATE"
        const val VERIFICATION_CODE_SESSION_TYPE = "VERIFICATION_CODE_SESSION_TYPE"

        /**
         * 生成验证码
         */
        @JvmStatic
        fun render(): String {
            val verificationCode = verificationGeneratorProvider!!.render()

            session!!.setAttribute(VERIFICATION_CODE_SESSION_KEY, verificationCode.code)
            session!!.setAttribute(VERIFICATION_CODE_SESSION_DATE, LocalDateTime.now())
            session!!.setAttribute(VERIFICATION_CODE_SESSION_TYPE, verificationCode.verificationType)

            // 只有图片验证码才执行流输出
            if (verificationCode.verificationType == VerificationType.IMAGE) {
                response!!.setDateHeader(HttpHeaders.EXPIRES, 0L)
                response!!.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                response!!.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0")
                response!!.setHeader(HttpHeaders.PRAGMA, "no-cache")
                response!!.contentType = MediaType.IMAGE_JPEG_VALUE
                response!!.outputStream.use {
                    ImageIO.write(verificationCode.image, "JPEG", it)
                }
            }

            return verificationCode.code
        }

        /**
         * 验证码校验
         * @param code                              用户输入的验证码
         * @param expireInSeconds                   验证码有效期(秒),默认60
         * @param cleanupVerificationInfoWhenWrong  验证码输入错误时,是否作废之前的验证码信息,默认false,当验证码类型为IMAGE时固定为true
         * @param throwException                    验证不通过时,是否抛出异常,默认false
         * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
         */
        @JvmStatic
        @JvmOverloads
        @Throws(
                VerificationNotFountException::class,
                VerificationWrongException::class,
                VerificationExpiredException::class
        )
        fun validate(
                code: String,
                expireInSeconds: Long = 60,
                cleanupVerificationInfoWhenWrong: Boolean = false,
                throwException: Boolean = false
        ): VerificationStatus {
            val sessionCode = (session!!.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_KEY)
                    ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as String
            val codeCreatedTime = (session!!.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_DATE)
                    ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as LocalDateTime
            val verificationType = (session!!.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_TYPE)
                    ?: return if (throwException) throw VerificationNotFountException() else VerificationStatus.NOT_FOUNT) as VerificationType

            return verificationValidateProvider!!.render(
                    request!!,
                    response!!,
                    session!!,
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
}