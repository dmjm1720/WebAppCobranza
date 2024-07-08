package mx.report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;

public class reportePolizaPagos extends mx.conexion.DAO {

    public void getReporte(String ruta, String folio) {
        Conectar();

        Map parameter = new HashMap();
        parameter.put("folio", folio);
        try {
            File file = new File(ruta);

            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"reporte.xlsx\";");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.setHeader("Pragma", "no-cache");
            httpServletResponse.setDateHeader("Expires", 0L);
            httpServletResponse.setContentType("application/XLSX");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(file.getPath());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, getCn());

            JRXlsxExporter jrExporter = new JRXlsxExporter();
            jrExporter = new JRXlsxExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, httpServletResponse.getOutputStream());

            if (jrExporter != null) {
                try {
                    jrExporter.exportReport();
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getCn() != null) {
                try {
                    Cerrar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
