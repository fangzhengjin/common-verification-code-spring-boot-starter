package com.github.fangzhengjin.common.component.verification.service

/**
 * @version V1.0
 * @title: VerificationStatus
 * @package com.github.fangzhengjin.common.component.verification.service
 * @description:
 * @author fangzhengjin
 * @date 2019/2/26 17:26
 */
enum class VerificationStatus(val message: String) {
    NOT_FOUNT("认证信息未找到"),
    SUCCESS("验证通过"),
    WRONG("验证码错误"),
    EXPIRED("验证码已过期")
}