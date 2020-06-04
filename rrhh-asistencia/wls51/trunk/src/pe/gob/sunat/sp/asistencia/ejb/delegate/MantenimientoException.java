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

public class MantenimientoException extends IncompleteConversationalState {

  public MantenimientoException(){
      super();
  }

  public MantenimientoException(String msg){
      super(msg);
  }

  public MantenimientoException(BeanMensaje beanMensaje){
      super(beanMensaje);
  }
}