package agentes.mailer;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

    private static Session sessao;
    private static String remetente;
    private static Properties props = new Properties();

    public static void setSessao(final String user, final String senha) {

        remetente = user;

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.live.com"); //hotmail
//        props.put("mail.smtp.host", "smtp.gmail.com"); //gmail
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", "*");

        sessao = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, senha);
            }
        });

        sessao.setDebug(false);
    }

    public static boolean enviar(String para, String titulo, String mensagem) {
        try {

            Message message = new MimeMessage(sessao);
            message.setFrom(new InternetAddress(remetente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(para));
            message.setSubject(titulo);
            message.setText(mensagem);
            message.setHeader("Content-Type", "text/html");

            Transport.send(message);
            
            System.out.println("Email enviado para: " + para);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
        Email email = new Email();
        email.setSessao("soonho.bot@outlook.com", "iamahuman!");
        System.out.println(email.enviar("georgerr@hotmail.com", "Soon Ho Bot 2", "<br>a<br><p>susanhausen</p>housenflausen"));
//        email.setSessao("georgerr@hotmail.com", "angelbeats!");
//        System.out.println(email.enviar("geosoonho@facebook.com", "Soon Ho Bot 2", "susanhausenhousenflausen"));
    }
}
