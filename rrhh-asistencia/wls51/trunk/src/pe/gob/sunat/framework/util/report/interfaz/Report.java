/**
 * 
 */
 package pe.gob.sunat.framework.util.report.interfaz;

/**
 * <p>Esta clase es una interface a las distintas implementaciones de Reportes
 * que se puedan obtener 
 *
 * @author FVILA
 * @deprecated @see pe.gob.sunat.framework.util.io.generador.service.GeneradorServiceImpl
 */
public abstract interface Report  { 
  
	/**
     * @deprecated 
     *
	 */
	public void ExportarPDF();
    /**
     * @deprecated
     *
     */
	public void ExportarXLS();
  
    public void exportarPDF();
    public void exportarXLS();
    public void exportarHTML();
    public void exportarRTF();

  
}
