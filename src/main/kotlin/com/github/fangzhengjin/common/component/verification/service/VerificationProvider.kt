package com.github.fangzhengjin.common.component.verification.service

import com.github.fangzhengjin.common.component.verification.vo.VerificationCode

/**
 * @version V1.0
 * @title: VerificationProvider
 * @package com.github.fangzhengjin.common.component.verification.service
 * @description: 验证码提供者
 * @author fangzhengjin
 * @date 2019/2/26 16:56
 */
interface VerificationProvider {
    fun render(): VerificationCode
}