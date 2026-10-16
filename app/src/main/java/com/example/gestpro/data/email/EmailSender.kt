package com.example.gestpro.data.email

import com.example.gestpro.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender {

    suspend fun sendPasswordReset(to: String, resetLink: String) = withContext(Dispatchers.IO) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(BuildConfig.SMTP_USER, BuildConfig.SMTP_PASSWORD)
        })

        val html = """
            <div style="font-family:Arial,sans-serif">
              <h2 style="color:#005A0B">GestPro</h2>
              <p>Recebemos um pedido para redefinir a sua senha.</p>
              <p><a href="$resetLink"
                    style="background:#005A0B;color:#fff;padding:12px 24px;border-radius:12px;text-decoration:none">
                    Redefinir senha</a></p>
              <p>O link expira em 30 minutos. Se você não fez esse pedido, ignore este email.</p>
            </div>
        """.trimIndent()

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(BuildConfig.SMTP_USER, "GestPro"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            setSubject("Redefinição de senha - GestPro", "UTF-8")
            setContent(html, "text/html; charset=utf-8")
        }
        Transport.send(message)
    }
}