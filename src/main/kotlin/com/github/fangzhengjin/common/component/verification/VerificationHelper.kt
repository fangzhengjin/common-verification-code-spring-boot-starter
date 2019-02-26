package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import com.github.fangzhengjin.common.component.verification.service.VerificationProvider
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.LocalDateTime
import javax.imageio.ImageIO
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
        private val response: HttpServletResponse,
        private val session: HttpSession,
        private val verificationProvider: VerificationProvider
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        const val VERIFICATION_CODE_SESSION_KEY = "VERIFICATION_CODE_SESSION_KEY"
        const val VERIFICATION_CODE_SESSION_DATE = "VERIFICATION_CODE_SESSION_DATE"
    }

    /**
     * 生成验证码
     */
    fun render(): String {
        val verificationCode = verificationProvider.render()

        response.setDateHeader(HttpHeaders.EXPIRES, 0L)
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
        response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0")
        response.setHeader(HttpHeaders.PRAGMA, "no-cache")
        response.contentType = MediaType.IMAGE_JPEG_VALUE

        session.setAttribute(VERIFICATION_CODE_SESSION_KEY, verificationCode.code)
        session.setAttribute(VERIFICATION_CODE_SESSION_DATE, LocalDateTime.now())

        response.outputStream.use {
            ImageIO.write(verificationCode.image, "JPEG", it)
        }

        return verificationCode.code
    }

    /**
     * 验证码校验
     */
    @JvmOverloads
    fun validate(code: String, expireInSeconds: Long = 60): VerificationStatus {
        val sessionCode = (session.getAttribute(VERIFICATION_CODE_SESSION_KEY) ?: return VerificationStatus.NOT_FOUNT) as String
        val codeCreatedTime = (session.getAttribute(VERIFICATION_CODE_SESSION_DATE) ?: return VerificationStatus.NOT_FOUNT) as LocalDateTime
        if (LocalDateTime.now().isAfter(codeCreatedTime.plusSeconds(expireInSeconds))) {
            return VerificationStatus.EXPIRED
        }
        if (!code.equals(sessionCode, true)) {
            return VerificationStatus.WRONG
        }
        return VerificationStatus.SUCCESS
    }

}