package com.github.fangzhengjin.common.autoconfigure.verification

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "customize.common.verification.recaptcha")
class ReCaptchaProperties {
    var secret: String = ""
    var host: GoogleReCaptchaHostType = GoogleReCaptchaHostType.RECAPTCHA_GOOGLE_CN
}

enum class GoogleReCaptchaHostType(
        val host: String
) {
    WWW_GOOGLE_COM("www.google.com"),
    WWW_RECAPTCHA_NET("www.recaptcha.net"),
    RECAPTCHA_GOOGLE_CN("recaptcha.google.cn")
}