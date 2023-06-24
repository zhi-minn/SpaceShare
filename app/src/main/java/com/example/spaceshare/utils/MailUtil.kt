package com.example.spaceshare.utils

import android.util.Log
import com.example.spaceshare.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object MailUtil {

    const val email = BuildConfig.SPACESHARE_EMAIL
    const val password = BuildConfig.SPACESHARE_PASSWORD

     fun sendEmail(to: String, subject: String, body: String) {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getDefaultInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(email))
        message.setRecipient(Message.RecipientType.TO, InternetAddress(to))
        message.subject = subject
        message.setText(body)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Transport.send(message)
                Log.i("MailUtil", "Email Sent")
            } catch (e: Exception) {
                Log.e("MailUtil", "Error sending email: ${e.message}", e)
            }
        }
    }
}