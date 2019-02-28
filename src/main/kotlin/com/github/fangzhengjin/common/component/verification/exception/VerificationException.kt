package com.github.fangzhengjin.common.component.verification.exception

import com.github.fangzhengjin.common.component.verification.service.VerificationStatus

/**
 * @version V1.0
 * @title: VerificationException
 * @package com.github.fangzhengjin.common.component.verification.exception
 * @description:
 * @author fangzhengjin
 * @date 2019/2/27 11:32
 */
open class VerificationException(message: String) : RuntimeException(message)

class VerificationWrongException(message: String = VerificationStatus.WRONG.message) : VerificationException(message)

class VerificationExpiredException(message: String = VerificationStatus.EXPIRED.message) : VerificationException(message)

class VerificationNotFountException(message: String = VerificationStatus.NOT_FOUNT.message) : VerificationException(message)