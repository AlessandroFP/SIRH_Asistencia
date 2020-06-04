package pe.gob.sunat.sp.asistencia.ejb.delegate;

import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;

/**
 * <p>Title: Control de Asistencia</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */

public class UtilesException extends IncompleteConversationalState {

  public UtilesException(){
      super();
  }

  public UtilesException(String msg){
      super(msg);
  }

  public UtilesException(BeanMensaje beanMensaje){
      super(beanMensaje);
  }
}