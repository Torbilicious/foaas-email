package de.torbilicious.main

import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

val username: String? = System.getenv("USERNAME")
val password: String? = System.getenv("PASSWORD")
val receiver: String? = System.getenv("RECEIVER")
val sender = username

fun sendMail(body: String) {

    val props = Properties()
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.socketFactory.port", "465")
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.port", "465")

    val session = Session.getDefaultInstance(props,
        object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })


    try {
        val message = MimeMessage(session)

        message.setFrom(InternetAddress(sender))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver))

        message.subject = "Wichtige Abrechnung - Bestimmt kein Virus"
        message.setText(body)

        Transport.send(message)
    } catch (e: MessagingException) {
        throw RuntimeException(e)
    }

}
