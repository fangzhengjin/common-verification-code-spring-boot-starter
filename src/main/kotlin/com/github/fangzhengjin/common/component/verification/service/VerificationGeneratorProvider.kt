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
    /**
     * 生成验证码
     */
    fun render(): VerificationCode

    /**
     * 是否支持该类型的验证码生成
     */
    fun isSupports(verificationType: VerificationType): Boolean
}