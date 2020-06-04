package pe.gob.sunat.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */

public class DescargarArchivo {

	private static final Logger log = Logger.getLogger(DescargarArchivo.class);
	
	public static int ZIP = 0;
	public static int OCTET = 1;
	public static int PDF = 2;

	private static String[] tipos = { "application/x-zip-compressed",
			"application/octet", "application/pdf" };

	public DescargarArchivo() {
	}

	/**
	 * Metodo encargado de descargar un archivo
	 * 
	 * @param datos
	 * @param nombre
	 * @param tipoarchivo
	 * @param response
	 * @throws java.lang.Exception
	 */
	public void descargarAPantalla(InputStream datos, String nombre,
			int tipoarchivo, HttpServletResponse response) throws Exception {

		try {

			OutputStream out = response.getOutputStream();
			response.setContentType(tipos[tipoarchivo]);
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ nombre + '\"');

			int b = 0;
			while ((b = datos.read()) != -1) {
				out.write(b);
			}
			datos.close();
			out.flush();
			out.close();

		} catch (Exception e) {
			log.error("*** Error ***",e);
			response.setContentType("text/html");
			throw new Exception("Error al descargar archivo.");
		}
	}

	/**
	 * Metodo encargado de descargar un archivo
	 * 
	 * @param datos
	 * @param nombre
	 * @param tipoarchivo
	 * @param response
	 * @throws java.lang.Exception
	 */
	public void descargarAPantalla(byte[] datos, String nombre,
			String contentType, HttpServletResponse response) throws Exception {

		try {

			OutputStream out = response.getOutputStream();
			response.setContentType(contentType);
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ nombre + '\"');

			out.write(datos);
			out.flush();
			out.close();

		} catch (Exception e) {
			log.error("*** Error ***",e);
			response.setContentType("text/html");
			throw new Exception("Error al descargar archivo.");
		}
	}

	/**
	 * Metodo encargado de descargar un archivo
	 * 
	 * @param datos
	 * @param nombre
	 * @param tipoarchivo
	 * @param response
	 * @throws java.lang.Exception
	 */
	public void descargarAPantalla(ByteArrayOutputStream datos, String nombre,
			int tipoarchivo, HttpServletResponse response) throws Exception {

		try {

			OutputStream out = response.getOutputStream();
			response.setContentType(tipos[tipoarchivo]);
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ nombre + '\"');
			datos.writeTo(out);
			datos.close();
			out.flush();
			out.close();

		} catch (Exception e) {
			log.error("*** Error ***",e);
			response.setContentType("text/html");
			throw new Exception("Error al descargar archivo.");
		}
	}

}