package pe.gob.sunat.personalizer.mail.dao;

import java.util.HashMap;

import pe.gob.sunat.personalizer.mail.bean.BeanCorreo;
import pe.gob.sunat.utils.jms.MessageException;
import pe.gob.sunat.utils.jms.QueueMessageSender;

public class DAOEnviaCorreo {
  public DAOEnviaCorreo() {
  }

  public void enviarCorreo(BeanCorreo beanCorreo, String queueCorreo) {
    try {

      HashMap parametros = new HashMap();
      parametros.put("beanCorreo", beanCorreo);

      QueueMessageSender sender = new QueueMessageSender(
          queueCorreo);
      sender.send(parametros);
      sender.close();
    }
    catch (MessageException ex) {
      ex.printStackTrace();
    }
  }

}