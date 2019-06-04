package com.github.fangzhengjin.common.component.verification.vo

import com.github.fangzhengjin.common.component.verification.service.VerificationType
import java.awt.image.BufferedImage

/**
 * @version V1.0
 * @title: VerificationCode
 * @package com.github.fangzhengjin.common.component.verification.vo
 * @description:
 * @author fangzhengjin
 * @date 2019/2/26 17:02
 */
data class VerificationCode @JvmOverloads constructor(
        val code: String,
        val image: BufferedImage? = null,
        val verificationType: VerificationType = VerificationType.IMAGE
)