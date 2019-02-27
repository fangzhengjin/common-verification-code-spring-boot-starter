package com.github.fangzhengjin.common.component.verification.service

import com.github.fangzhengjin.common.component.verification.vo.VerificationCode

/**
 * @version V1.0
 * @title: VerificationGeneratorProvider
 * @package com.github.fangzhengjin.common.component.verification.service
 * @description: 验证码创建提供者
 * @author fangzhengjin
 * @date 2019/2/26 16:56
 */
interface VerificationGeneratorProvider {
    fun render(): VerificationCode
}