package com.github.fangzhengjin.common.component.verification.service.impl

import com.github.fangzhengjin.common.component.verification.exception.VerificationExpiredException
import com.github.fangzhengjin.common.component.verification.exception.VerificationNotFountException
import com.github.fangzhengjin.common.component.verification.exception.VerificationWrongException
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import com.github.fangzhengjin.common.component.verification.service.VerificationType
import com.github.fangzhengjin.common.component.verification.service.VerificationValidatorProvider
import com.github.fangzhengjin.common.component.verification.vo.VerificationValidateData
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * @version V1.0
 * @title: DefaultVerificationGeneratorProvider
 * @package com.github.fangzhengjin.common.component.verification.service.impl
 * @description:
 * @author fangzhengjin
 * @date 2019/2/26 17:39
 */
class DefaultVerificationValidatorProvider : VerificationValidatorProvider {
    /**
     * 是否支持该类型的验证码验证
     */
    override fun isSupports(verificationType: VerificationType): Boolean {
        return verificationType == VerificationType.IMAGE
    }

    /**
     * 验证码校验
     * @param session                           当前请求的session
     * @param request                           当前请求的request
     * @param response                          当前请求的response
     * @param verificationValidateData          验证信息
     * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
     */
    @Throws(
            VerificationNotFountException::class,
            VerificationWrongException::class,
            VerificationExpiredException::class
    )
    override fun render(
            request: HttpServletRequest,
            response: HttpServletResponse,
            session: HttpSession,
            verificationValidateData: VerificationValidateData
    ): VerificationStatus {
        if (verificationValidateData.isExpire) {
            removeSessionVerificationInfo(session)
            return if (verificationValidateData.throwException) throw VerificationExpiredException() else VerificationStatus.EXPIRED
        }
        if (!verificationValidateData.userInputCode.equals(verificationValidateData.sessionCode, true)) {
            // 如果是图片类型验证码 并且没有设置cleanupVerificationInfoWhenWrong则默认清理
            if (verificationValidateData.cleanupVerificationInfoWhenWrong) removeSessionVerificationInfo(session)
            return if (verificationValidateData.throwException) throw VerificationWrongException() else VerificationStatus.WRONG
        }
        removeSessionVerificationInfo(session)
        return VerificationStatus.SUCCESS
    }
}