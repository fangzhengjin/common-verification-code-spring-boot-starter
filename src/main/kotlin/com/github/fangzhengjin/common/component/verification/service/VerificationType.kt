package com.github.fangzhengjin.common.component.verification.service

/**
 * @version V1.0
 * @title: VerificationType
 * @package com.github.fangzhengjin.common.component.verification.service
 * @description:
 * @author fangzhengjin
 * @date 2019/2/27 9:37
 */
enum class VerificationType(
        val description: String
) {
    IMAGE("图形验证码"),
    SMS("短信验证码"),
    MAIL("邮箱验证码")
}