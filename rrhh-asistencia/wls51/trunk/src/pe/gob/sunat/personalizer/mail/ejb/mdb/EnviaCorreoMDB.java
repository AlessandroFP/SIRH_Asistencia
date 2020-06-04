package pe.gob.sunat.personalizer.mail.ejb.mdb;

import java.util.HashMap;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import pe.gob.sunat.personalizer.mail.bean.BeanCorreo;

/**
 * @ejb.bean name="EnviaCorreoMDB"
 *      		description="Maneja la cola de correos"
 *      		local-jndi-name="ejb/mdb/correo/EnviaCorreoMDB"
 *      		destination-type="javax.jms.Queue"
 *      		subscription-durability="NonDurable"
 * 
 * @ejb.transaction-type type="Container"
 *
 * @jboss.container-configuration name="Standard Message Driven Bean"
 * @jboss.destination-jndi-name name="queue/CorreoQueue"
 * 
 *
 * @weblogic.message-driven connection-factory-jndi-name="RRHHConnectionFactory"
 *     						destination-jndi-name="queue/CorreoQueue"
 *
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.pool 	initial-beans-in-free-pool="1" max-beans-in-free-pool="3"
 * @weblogic.pool max-beans-in-free-pool="10" initial-beans-in-free-pool="5"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @jboss.version="3.2"
 * @author  cgarratt
 * <p>Copyright: Copyright (c) 2004</p>
 * @version 1.0
*/
public class EnviaCorreoMDB implements MessageDrivenBean, MessageListener {

    private static final Logger log = Logger.getLogger(EnviaCorreoMDB.class);
    
    private MessageDrivenContext context;

    public void ejbCreate(){}

    public void ejbRemove(){}

    public void setMessageDrivenContext(MessageDrivenContext context) {
      this.context = context;
    }
    
    public void onMessage(Message message){
        try {

        	ObjectMessage obj = (ObjectMessage)message;
            HashMap hmParametros = (HashMap) obj.getObject();
            BeanCorreo beanCorreo = (BeanCorreo) hmParametros.get("beanCorreo");

            javax.mail.Session mailSession = (javax.mail.Session) new InitialContext()
                    .lookup("mail/CorreoInterno");
            javax.mail.Message msg = new MimeMessage(mailSession);
            if (beanCorreo.getBuzon_bcc() != null) {
                msg.addRecipients(javax.mail.Message.RecipientType.BCC,
                        InternetAddress.parse(beanCorreo.getBuzon_bcc(), false));
            }
            msg.addRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(beanCorreo.getBuzon_para(), false));
            msg.setSubject(beanCorreo.getAsunto());
            msg.setContent(beanCorreo.getMensaje()," text/html; charset=iso-8859-1");
            msg.setHeader("X-Mailer", "JavaMailer");
            msg.setSentDate(new java.util.Date());
            Transport.send(msg);
        
        }catch (javax.mail.internet.AddressException e) {
            log.error("La direccion de correo obtenida del LDAP no es valida",e);           
        } catch (javax.mail.MessagingException e) {
            log.error("Ha ocurrido un problema al enviar el correo de confirmacion",e);            
        } catch (NamingException e) {
            log.error("No se ha podido conectar al servicio de mensajeria para enviar el correo de confirmacion",e);            
        } catch (Exception e) {
        	log.error("No se ha podido conectar al servicio de mensajeria para enviar el correo de confirmacion",e);            
        }
    }

}