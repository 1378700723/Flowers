/**
 * create by 朱施健
 */
package org.guyou.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * @author 朱施健
 *
 */
public class MailUtil {
    
    /**
     * 发送邮件
     * @param mail_smtp_host
     * @param mail_sender_username
     * @param mail_sender_password
     * @param title
     * @param content
     * @param attachment
     * @param mail_receive_usernames
     * @return
     * @throws MessagingException 
     * @throws UnsupportedEncodingException 
     */
    public static void sendMail(
    			boolean isDebug,
    			String mail_smtp_host,
    			String mail_sender_username,
    			String mail_sender_password,
    			String title,
    			String content,
    			File attachment,
    			String... mail_receive_usernames
    		) throws MessagingException, UnsupportedEncodingException{
    	
    	if(mail_receive_usernames.length==0) return ;
    	
    	Properties properties = new Properties();
    	properties.put("mail.smtp.host", mail_smtp_host);
        properties.put("mail.sender.username", mail_sender_username);
        properties.put("mail.sender.password", mail_sender_password);
        
        Session session = Session.getInstance(properties);
        session.setDebug(isDebug);// 开启后有调试信息
        
        MimeMessage message = new MimeMessage(session);
        
        // 发件人
        message.setFrom(new InternetAddress(mail_sender_username));

        for (String m_address : mail_receive_usernames) {
        	// 收件人
        	message.addRecipient(Message.RecipientType.TO, new InternetAddress(m_address));
		}

        // 邮件主题
        message.setSubject(title);
        
        // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
        Multipart multipart = new MimeMultipart();
        
        // 添加邮件正文
        BodyPart contentPart = new MimeBodyPart();
        contentPart.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);
        
        // 添加附件的内容
        if (attachment != null && attachment.exists() && attachment.exists()) {
            BodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachment);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            //MimeUtility.encodeWord可以避免文件名乱码
            attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
            multipart.addBodyPart(attachmentBodyPart);
        }
        
        // 将multipart对象放到message中
        message.setContent(multipart);
        // 保存邮件
        message.saveChanges();
        
        Transport transport = null;
        try {
        	transport = session.getTransport("smtp");
        	// smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mail_smtp_host, mail_sender_username, mail_sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());
		} finally {
			if (transport != null) {
				transport.close();
            }
		}
    }

    public static void main(String[] args) throws UnsupportedEncodingException, MessagingException {
    	MailUtil.sendMail(true,"smtp.qq.com", "26139546@qq.com", "111111", "JMail测试", "你好 我好 他好 大家好 <a href=\"\">链接</a>", new File("D:\\封测数据统计.xlsx"), "zhushijian@colomob.com","26139546@qq.com","a@a.com");
    }
}
