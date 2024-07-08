package mx.report;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import mx.conexion.DAO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class reportePoliza
        extends DAO {

    public void getReporte(String ruta, String nombre, String folio) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Conectar();
        Map parameter = new HashMap();
        parameter.put("nombreReporte", nombre);
        parameter.put("nombreFolio", folio);
        try {
            File file = new File(ruta);

            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\\Poliza-" + nombre + "-" + folio + ".txt");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setDateHeader("Expires", 0L);
            httpServletResponse.setContentType("application/TEXT");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(file.getPath());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, getCn());

            JRExporter jrExporter = null;
            jrExporter = new JRTextExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, httpServletResponse.getOutputStream());
            jrExporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Float(2.5D));
            jrExporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Float(8.0F));

            if (jrExporter != null) {
                try {
                    jrExporter.exportReport();
                } catch (JRException e) {
                    System.err.println(e.getMessage());
                }
            }
            return;
        } catch (JRException | IOException localJRException1) {
        } finally {
            if (getCn() != null) {
                try {
                    Cerrar();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
