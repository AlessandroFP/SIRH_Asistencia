package pe.gob.sunat.rrhh.utils;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase que permite convertir los archivos de excel (.xlsx) a una lista.
 * 
 * @version 1.0
 * @author jmaravi
 * @since 22/04/2014
 */
public class FileConverter{
	protected final Log log = LogFactory.getLog(getClass());
	
	public FileConverter(){}

	
//TODO Habilitar este método cuando actualicen la APP al JDK 1.6	
	/**
	 * Lee el archivo excel enviado y lo devuelve como un List de Lists (matriz) que representa
	 * el archivo excel adjuntado.
	 * @param fileName
	 * @return Lista que contiene a su vez listas que representan las celdas de cada fila del archivo
	 * @author jmaravi
	 * @since 22/04/2014
	 */
/*	
	public List convertXlsxToList(File fileNameXlsx) {
		List cellDataList = new ArrayList();
		FileInputStream fileInputStream = null;
		XSSFWorkbook workBook = null;
		try {
			fileInputStream = new FileInputStream(fileNameXlsx);
			workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet hssfSheet = workBook.getSheetAt(0);
			Iterator rowIterator = hssfSheet.rowIterator();
			while (rowIterator.hasNext()) {
				XSSFRow hssfRow = (XSSFRow) rowIterator.next();
				Iterator iterator = hssfRow.cellIterator();
				List cellTempList = new ArrayList();
				while (iterator.hasNext()) {
					XSSFCell hssfCell = (XSSFCell) iterator.next();					
					cellTempList.add(hssfCell.toString());
				}
				cellDataList.add(cellTempList);
			}
		} catch (Exception e) {
			log.error("ERROR: "+e.getMessage());			
		}finally{
			if(fileInputStream != null){
				//fileInputStream.close();				
			}
		}
		if(log.isDebugEnabled()) log.debug("FileConverter: El archivo tenía "+cellDataList.size()+" filas.");
		return cellDataList;
	}
*/
	/**
	 * Lee el archivo excel enviado y lo devuelve como un List de Lists (matriz) que representa
	 * el archivo excel adjuntado, Usando el formato de Flujo de Aprobadores masivo.
	 * @param fileName
	 * @return Lista que contiene a su vez listas que representan las celdas de cada fila del archivo
	 * @author jmaravi
	 * @since 22/04/2014
	 */
	public List flujoAprobadoresXlsxToList(File fileNameXlsx,Map dbean) {
		List fileDataList = new ArrayList();
		FileInputStream fileInputStream = null;
		XSSFWorkbook workBook = null;
		
		try {
			fileInputStream = new FileInputStream(fileNameXlsx);
			workBook = new XSSFWorkbook(fileInputStream);
			XSSFSheet hssfSheet = workBook.getSheetAt(0);
			String[] cabecera = (String[])dbean.get("cabeceraXlsx");
			int rowIniData = ((Integer) dbean.get("rowIniData")).intValue();
			//Iterator rowIterator = hssfSheet.rowIterator();
			int numFilas = hssfSheet.getLastRowNum() + 1;
			int numColums= 0;
			HashMap hmFila=null;
			//String[] cabecera = new String[]{"UniOrg", "Instancia1", "Instancia2","Instancia3"};
			
			//Segun el formato la data se contiene desde la fila 3
			for(int y=rowIniData-1;y<numFilas;y++){
				XSSFRow hssfRow = (XSSFRow) hssfSheet.getRow(y);
				hmFila= new HashMap();
				
				//numColums = hssfRow.getLastCellNum();
				numColums = cabecera.length;
				for(int x=0;x<numColums;x++) {
					XSSFCell hssfCell = (XSSFCell) hssfRow.getCell(x);
					hmFila.put(cabecera[x],hssfCell==null?"": hssfCell.toString().trim());					
				}
				hmFila.put("estado", "OK");
				fileDataList.add(hmFila);
			}
		} catch (Exception e) {
			log.error("ERROR: "+e.getMessage());			
		}finally{
			if(fileInputStream != null){
				//fileInputStream.close();				
			}
		}
		if(log.isDebugEnabled()) log.debug("FileConverter: El archivo tenía "+fileDataList.size()+" filas.");
		return fileDataList;
	}	
}
