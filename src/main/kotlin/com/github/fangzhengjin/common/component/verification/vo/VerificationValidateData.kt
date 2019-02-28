package com.github.fangzhengjin.common.component.verification.vo

import com.github.fangzhengjin.common.component.verification.service.VerificationType

/**
 * @version V1.0
 * @title: VerificationValidateData
 * @package com.github.fangzhengjin.common.component.verification.vo
 * @description:
 * @author fangzhengjin
 * @date 2019/2/27 14:50
 */

/**
 * @param sessionCode                       Session中的验证码
 * @param userInputCode                     用户输入的验证码
 * @param verificationType                  验证码类型，默认图形验证码
 * @param isExpire                          验证码是否已过期
 * @param cleanupVerificationInfoWhenWrong  验证码输入错误时,是否作废之前的验证码信息,默认false,当验证码类型为IMAGE时固定为true
 * @param throwException                    验证不通过时,是否抛出异常,默认false
 */
data class VerificationValidateData @JvmOverloads constructor(
        val sessionCode: String,
        val userInputCode: String,
        val verificationType: VerificationType = VerificationType.IMAGE,
        val isExpire: Boolean = false,
        val cleanupVerificationInfoWhenWrong: Boolean = false,
        val throwException: Boolean = false
) {
}