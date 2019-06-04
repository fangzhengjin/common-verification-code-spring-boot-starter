package com.github.fangzhengjin.common.component.verification.service.impl.generator

import com.github.fangzhengjin.common.component.verification.service.VerificationGeneratorProvider
import com.github.fangzhengjin.common.component.verification.service.VerificationType
import com.github.fangzhengjin.common.component.verification.vo.VerificationCode

/**
 * @version V1.0
 * @title: DefaultMailVerificationGeneratorProvider
 * @package com.github.fangzhengjin.common.component.verification.service.impl.generator
 * @description:
 * @author fangzhengjin
 * @date 2019/2/26 17:39
 */
class DefaultMailVerificationGeneratorProvider : VerificationGeneratorProvider {
    /**
     * 是否支持该类型的验证码生成
     */
    override fun isSupports(verificationType: VerificationType): Boolean {
        return verificationType == VerificationType.MAIL
    }

    override fun render(): VerificationCode {
        val verifyCode = String.format("%.0f", (Math.random() * 9 + 1) * 100000)
        return VerificationCode(code = verifyCode, verificationType = VerificationType.MAIL)
    }
}