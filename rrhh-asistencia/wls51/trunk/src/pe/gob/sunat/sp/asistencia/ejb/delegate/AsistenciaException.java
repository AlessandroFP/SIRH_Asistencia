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

public class AsistenciaException extends IncompleteConversationalState {

  public AsistenciaException(){
      super();
  }

  public AsistenciaException(String msg){
      super(msg);
  }

  public AsistenciaException(BeanMensaje beanMensaje){
      super(beanMensaje);
  }
}