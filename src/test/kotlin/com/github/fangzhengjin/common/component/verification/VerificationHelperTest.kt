package com.github.fangzhengjin.common.component.verification

import com.github.fangzhengjin.ApplicationTests
import com.github.fangzhengjin.common.component.verification.exception.VerificationException
import com.github.fangzhengjin.common.component.verification.exception.VerificationExpiredException
import com.github.fangzhengjin.common.component.verification.exception.VerificationNotFountException
import com.github.fangzhengjin.common.component.verification.exception.VerificationWrongException
import com.github.fangzhengjin.common.component.verification.service.VerificationStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.servlet.http.HttpSession

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [ApplicationTests::class])
class VerificationHelperTest {

    @Autowired
    private lateinit var httpSession: HttpSession

    @Test
    fun render() {
        val renderCode = VerificationHelper.render()
        val sessionCode = httpSession.getAttribute(VerificationHelper.VERIFICATION_CODE_SESSION_KEY)
        Assert.assertNotNull("Session中未找到验证码信息", sessionCode)
        Assert.assertEquals(renderCode, sessionCode)
    }

    @Test
    fun validate() {
        var renderCode = VerificationHelper.render()
        var status = VerificationHelper.validate(code = renderCode, cleanupVerificationInfoWhenWrong = false)
        Assert.assertEquals(status, VerificationStatus.SUCCESS)

        renderCode = VerificationHelper.render()
        status = VerificationHelper.validate(code = "test$renderCode", cleanupVerificationInfoWhenWrong = false)
        Assert.assertEquals(status, VerificationStatus.WRONG)

        try {
            VerificationHelper.validate(code = "test$renderCode", cleanupVerificationInfoWhenWrong = false, throwException = true)
        } catch (e: VerificationException) {
            if (e !is VerificationWrongException) {
                Assert.fail("异常不符合预期，预期异常：VerificationWrongException")
            }
        }

        Thread.sleep(1000)
        status = VerificationHelper.validate(code = renderCode, expireInSeconds = 1L, cleanupVerificationInfoWhenWrong = false)
        Assert.assertEquals(status, VerificationStatus.EXPIRED)

        try {
            renderCode = VerificationHelper.render()
            Thread.sleep(1500)
            VerificationHelper.validate(code = "test$renderCode", expireInSeconds = 1L, cleanupVerificationInfoWhenWrong = true, throwException = true)
        } catch (e: VerificationException) {
            if (e !is VerificationExpiredException) {
                Assert.fail("异常不符合预期，预期异常：VerificationExpiredException")
            }
        }

        status = VerificationHelper.validate(code = renderCode, cleanupVerificationInfoWhenWrong = false)
        Assert.assertEquals(status, VerificationStatus.NOT_FOUNT)

        try {
            VerificationHelper.validate(code = "test$renderCode", expireInSeconds = 1L, cleanupVerificationInfoWhenWrong = false, throwException = true)
        } catch (e: VerificationException) {
            if (e !is VerificationNotFountException) {
                Assert.fail("异常不符合预期，预期异常：VerificationNotFountException")
            }
        }

        renderCode = VerificationHelper.render()
        status = VerificationHelper.validate(code = "test$renderCode", cleanupVerificationInfoWhenWrong = true)
        Assert.assertEquals(status, VerificationStatus.WRONG)

        status = VerificationHelper.validate(code = renderCode, cleanupVerificationInfoWhenWrong = false)
        Assert.assertEquals(status, VerificationStatus.NOT_FOUNT)
    }
}