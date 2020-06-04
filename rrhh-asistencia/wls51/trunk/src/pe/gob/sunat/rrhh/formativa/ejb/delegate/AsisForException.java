package pe.gob.sunat.rrhh.formativa.ejb.delegate;

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

public class AsisForException  extends IncompleteConversationalState {

  public AsisForException(){
      super();
  }

  public AsisForException(String msg){
      super(msg);
  }

  public AsisForException(BeanMensaje beanMensaje){
      super(beanMensaje);
  }
}