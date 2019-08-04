package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.common.component.verification.exception.*
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.Duration


/**
 * @version V1.0
 * @title: VerificationHelperWithGoogleReCaptcha
 * @package com.github.fangzhengjin.common.component.verification
 * @description: 验证码助手
 * @author fangzhengjin
 * @date 2019/2/26 16:34
 */
class VerificationHelperWithGoogleReCaptcha {

    class ResponseData {
        var challengeTs: Int? = null
        var errorCodes: List<String> = ArrayList()
        var hostname: String? = null
        var success: Boolean = false
    }

    companion object {
        @JvmStatic
        private val restTemplate: RestTemplate = RestTemplateBuilder()
                .setReadTimeout(Duration.ofSeconds(30))
                .setConnectTimeout(Duration.ofSeconds(30))
                .build()
        @JvmStatic
        private val errorCodes = hashMapOf(
                "missing-input-secret" to "The secret parameter is missing",
                "invalid-input-secret" to "The secret parameter is invalid or malformed",
                "missing-input-response" to "The response parameter is missing",
                "invalid-input-response" to "The response parameter is invalid or malformed",
                "bad-request" to "The request is invalid or malformed",
                "timeout-or-duplicate" to "The response is no longer valid: either is too old or has been used previously"
        )
    }

    /**
     * 验证码校验
     * @param token             验证令牌
     * @param throwException    验证不通过时,是否抛出异常,默认false
     * @return 如果选择验证不通过不抛出异常,则返回VerificationStatus验证状态枚举
     */
    @JvmOverloads
    @Throws(
            VerificationNotFountException::class,
            VerificationWrongException::class,
            VerificationExpiredException::class,
            VerificationNotFountExpectedValidatorProviderException::class
    )
    fun validate(token: String, throwException: Boolean = false): VerificationStatus {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        //提交参数
        val params = LinkedMultiValueMap<String, String>()
        params["secret"] = ""
        params["response"] = token
        val request = HttpEntity<MultiValueMap<String, String>>(params, headers)
        val responseEntity = restTemplate.postForEntity("https://www.recaptcha.net/recaptcha/api/siteverify", request, ResponseData::class.java)
        if (responseEntity.statusCode === HttpStatus.OK) {
            val responseData = responseEntity.body
                    ?: if (throwException) throw VerificationException("request error")
                    else return VerificationStatus.NOT_FOUNT
            if (responseData.success) {
                return VerificationStatus.SUCCESS
            }
        }
        return VerificationStatus.WRONG
    }
}