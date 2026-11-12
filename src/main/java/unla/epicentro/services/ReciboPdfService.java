package unla.epicentro.services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import unla.epicentro.dtos.DetalleLiquidacionDTO;
import unla.epicentro.entities.Persona;

@Service
public class ReciboPdfService {

	private static final DecimalFormat MONTO = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("es-AR")));
	private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
	private final Font negrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
	private final Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);
	private final Font chica = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9);

	public byte[] generar(Persona empleado, DetalleLiquidacionDTO detalle) {
		ByteArrayOutputStream salida = new ByteArrayOutputStream();
		Document documento = new Document();
		PdfWriter.getInstance(documento, salida);
		documento.open();

		Paragraph encabezado = new Paragraph("Recibo de sueldo - Epicentro Gourmet", titulo);
		encabezado.setAlignment(Element.ALIGN_CENTER);
		encabezado.setSpacingAfter(16);
		documento.add(encabezado);

		documento.add(new Paragraph("Empleado: " + empleado.getNombreCompleto() + " (DNI " + empleado.getDni() + ")", normal));
		documento.add(new Paragraph("Rol: " + detalle.rol(), normal));
		documento.add(new Paragraph("Festival: " + detalle.festival() + " (" + detalle.diasFestival() + " días)", normal));
		documento.add(new Paragraph("Unidad de venta: " + detalle.unidad(), normal));
		Paragraph emision = new Paragraph("Fecha de emisión: " + LocalDate.now().format(FECHA), normal);
		emision.setSpacingAfter(12);
		documento.add(emision);

		PdfPTable tabla = new PdfPTable(new float[] { 3, 1 });
		tabla.setWidthPercentage(100);
		fila(tabla, "Concepto", "Monto", negrita);
		fila(tabla, "Sueldo base", pesos(detalle.sueldoBase()), normal);
		fila(tabla, detalle.conceptoAdicional(), pesos(detalle.adicional()), normal);
		fila(tabla, "Subtotal", pesos(detalle.subtotal()), negrita);
		fila(tabla, "Factor por duración (" + detalle.diasFestival() + " días)", "× " + detalle.porcentajeFactor() + "%", normal);
		fila(tabla, "Total a cobrar", pesos(detalle.total()), negrita);
		documento.add(tabla);

		Paragraph nota = new Paragraph(detalle.descripcionFactor(), chica);
		nota.setSpacingBefore(8);
		documento.add(nota);

		documento.close();
		return salida.toByteArray();
	}

	private void fila(PdfPTable tabla, String concepto, String monto, Font fuente) {
		tabla.addCell(new PdfPCell(new Phrase(concepto, fuente)));
		PdfPCell celdaMonto = new PdfPCell(new Phrase(monto, fuente));
		celdaMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tabla.addCell(celdaMonto);
	}

	private String pesos(BigDecimal monto) {
		return "$ " + MONTO.format(monto);
	}
}
