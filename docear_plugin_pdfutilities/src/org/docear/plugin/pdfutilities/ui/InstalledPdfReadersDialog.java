package org.docear.plugin.pdfutilities.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.docear.plugin.core.ui.MultiLineActionLabel;
import org.docear.plugin.pdfutilities.PdfUtilitiesController;
import org.docear.plugin.pdfutilities.features.PDFReaderHandle;
import org.docear.plugin.pdfutilities.features.PDFReaderHandle.RegistryBranch;
import org.docear.plugin.pdfutilities.pdf.PdfReaderFileFilter;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class InstalledPdfReadersDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JComboBox readerChoice;
	private final PDFReaderHandle[] readerHandles;
	private JLabel pdfxcvWarning;

	public InstalledPdfReadersDialog(PDFReaderHandle[] handles, Boolean newReader) {
		readerHandles = handles;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(55dlu;min)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:default:grow"),}));
		
		JPanel pnlDialogInfo = new JPanel();
		pnlDialogInfo.setBackground(Color.WHITE);
		add(pnlDialogInfo, "1, 1, 2, 2, fill, fill");
		pnlDialogInfo.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:default:grow"),}));
		
		JLabel lblHeadline = new JLabel(TextUtils.getText("docear.validate_pdf_xchange.headline"));
		lblHeadline.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlDialogInfo.add(lblHeadline, "2, 2");
		
		JLabel lblNewLabel = null;
		if (newReader == null) {
			lblNewLabel = new JLabel(TextUtils.getText("docear.validate_pdf_xchange.info"));	
		}
		else if (newReader) {
			lblNewLabel = new JLabel(TextUtils.getText("docear.validate_pdf_xchange.info_new_reader"));			
		}
		else {
			lblNewLabel = new JLabel(TextUtils.getText("docear.validate_pdf_xchange.info_not_compatible"));
		}
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		pnlDialogInfo.add(lblNewLabel, "2, 4");
		
		JPanel pnlForm = new JPanel();
		add(pnlForm, "2, 4, fill, fill");
		pnlForm.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblPdfReaderList = new JLabel(TextUtils.getText("docear.validate_pdf_xchange.choose_label"));
		pnlForm.add(lblPdfReaderList, "2, 2, right, default");
		
		lblPdfReaderList.setLabelFor(getReaderChoiceBox());
		pnlForm.add(getReaderChoiceBox(), "4, 2, fill, default");				
		
		MultiLineActionLabel pdfxcvLink = new MultiLineActionLabel(TextUtils.getText("docear.help.pdf_xchange_viewer"));
		pdfxcvLink.addActionListener(new ActionListener() {				
			public void actionPerformed(ActionEvent e) {
				if ("open_url".equals(e.getActionCommand())) {
					try {
						Controller.getCurrentController().getViewController().openDocument(URI.create("http://www.tracker-software.com/product/pdf-xchange-viewer/"));
					}
					catch (IOException ex) {						
						LogUtils.warn(ex);
					}
				}
			}
		});	
		pdfxcvWarning = new JLabel(TextUtils.getText("docear.help.pdf_xchange_viewer.warning"));
		pdfxcvWarning.setForeground(Color.red);
		pdfxcvWarning.setVisible(false);		
		showWarningIfNecessary();
		
		MultiLineActionLabel helpLink = new MultiLineActionLabel(TextUtils.getText("docear.help.compatible_pdf_readers"));
		helpLink.addActionListener(new ActionListener() {				
			public void actionPerformed(ActionEvent e) {
				if ("open_url".equals(e.getActionCommand())) {
					try {
						Controller.getCurrentController().getViewController().openDocument(URI.create("http://www.docear.org/support/user-manual/#compatible_pdf_readers"));
					}
					catch (IOException ex) {						
						LogUtils.warn(ex);
					}
				}
			}
		});			
		
		pnlForm.add(pdfxcvLink, "2, 4, 3, 1");
		pnlForm.add(pdfxcvWarning, "2, 6, 3, 1");
		pnlForm.add(helpLink, "2, 8, 3, 1");		
	}

	public JComboBox getReaderChoiceBox() {
		if(readerChoice == null) {
			readerChoice = new JComboBox();
			
			readerChoice.setModel(new DefaultComboBoxModel(readerHandles));
			readerChoice.addItem(new PDFReaderHandle(TextUtils.getText("docear.default_reader"), "", RegistryBranch.DEFAULT));
			readerChoice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showWarningIfNecessary();
				}
			});
		} 
		return readerChoice;
	}
	
	private void showWarningIfNecessary() {
		String execFile = ((PDFReaderHandle) readerChoice.getSelectedItem()).getExecFile();
		boolean compatible = true;
		if (execFile != null) {
			PdfReaderFileFilter filter = new PdfReaderFileFilter();
			if (filter.isPdfXChange(execFile)) {							
				try {
					compatible = PdfUtilitiesController.getController().hasCompatibleSettings();
				}
				catch (IOException e1) {
				}
			}
		}
		if (!compatible) {
			pdfxcvWarning.setVisible(true);
		}
		else {
			pdfxcvWarning.setVisible(false);
		}
	}
}
