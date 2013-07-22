package org.tno.wpclient;

import java.awt.BorderLayout;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.wikipathways.webservice.WSCurationTag;
import org.pathvisio.wikipathways.webservice.WSPathwayInfo;
import org.pathvisio.wikipathways.webservice.WSSearchResult;
import org.wikipathways.client.WikiPathwaysClient;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class AdvancedSearchPanel extends JPanel {
	WikiPathwaysClientPlugin plugin;
	JTextField pTitleOrId;
	JComboBox clientDropdown;
	java.util.HashMap<String, String> curationtags = new HashMap<String, String>();
	JTable resultTable;
	int i;
	private JComboBox curationOpt;

	private JScrollPane resultspane;
	Border etch = BorderFactory.createEtchedBorder();
	private JLabel tipLabel;
	private JTextField txtId;
	private JComboBox cbSyscode;
	private JComboBox cbSearchBy;
	private Component symbolOpt;
	private JTextField pubref;

	public AdvancedSearchPanel(final WikiPathwaysClientPlugin plugin) {

		this.plugin = plugin;

		setLayout(new BorderLayout());
		pTitleOrId = new JTextField();
		pubref = new JTextField();

		Action searchAction = new AbstractAction("Search") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Pathways"));
					if (pTitleOrId.getText().startsWith("WP"))
						searchID();
					else {

						search();

					}
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(AdvancedSearchPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		Action searchRefAction = new AbstractAction(" searchRef") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Pathways"));

					searchPublicationRef();

				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(AdvancedSearchPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching WikiPathways", ex);
				}
			}

		};
		pTitleOrId.addActionListener(searchAction);
		pubref.addActionListener(searchRefAction);
		// tipLabel = new JLabel("Tip: pathway identifier, title, curator')");
		// tipLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		// pTitleOrId.setToolTipText("Enter any search query (e.g. 'Apoptosis' or 'P53').");
		// Curation Combobox
		/*
		 * curationtags.put("Not specified", "Not specified");
		 * curationtags.put("Curation:MissingXRef", "missing annotations");
		 * curationtags.put("Curation:NoInteractions", "unconnected lines");
		 * curationtags.put("Curation:UnderConstruction", "under construction");
		 * curationtags.put("Curation:Stub", "stub");
		 * curationtags.put("Curation:NeedsWork", "needs work"); curationOpt =
		 * new JComboBox(); Iterator<String> it =
		 * curationtags.keySet().iterator(); while (it.hasNext()) {
		 * curationOpt.addItem(curationtags.get(it.next())); }
		 * 
		 * curationOpt.setSelectedItem("Not specified"); DefaultFormBuilder
		 * curationOptBuilder = new DefaultFormBuilder( new
		 * FormLayout("right:pref, 3dlu,right:pref"));
		 * curationOptBuilder.append(curationOpt);
		 * curationOpt.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) { try {
		 * resultspane.setBorder(BorderFactory.createTitledBorder( etch,
		 * "Pathways"));
		 * 
		 * browseByCurationTag();
		 * 
		 * } catch (Exception ex) {
		 * JOptionPane.showMessageDialog(AdvancedSearchPanel.this,
		 * ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		 * Logger.log.error("Error searching WikiPathways", ex); } } });
		 * 
		 * final JPanel opts4 = new JPanel(); final CardLayout opt4Cards = new
		 * CardLayout(); opts4.setLayout(opt4Cards); JPanel curOpt =
		 * curationOptBuilder.getPanel(); opts4.add(curOpt, "Curation Tags");
		 */

		// preparing the container for the labels and comboboxes

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"p,3dlu,120px,2dlu,40px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(etch,
				"Search options"));
		searchOptBox.add(new JLabel("Title/ID"), cc.xy(1, 1));
		searchOptBox.add(pTitleOrId, cc.xyw(1, 2, 3));

		// searchOptBox.add(new JLabel("Curator Tag"), cc.xy(6, 1));
		searchOptBox.add(new JLabel("Publication Title/ID"), cc.xy(6, 1));
		searchOptBox.add(new JLabel("(OR)"), cc.xyw(5, 2, 1));
		searchOptBox.add(pubref, cc.xyw(6, 2, 3));
		// searchOptBox.add(opts4, cc.xyw(6, 2, 3));
		// JButton searchButton = new JButton(searchAction);
		// searchOptBox.add(searchButton, cc.xy(7, 1));

		// NEXT PANEL
		txtId = new JTextField();
		txtId.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				try {
					doSearch();
				} catch (RemoteException e) {

					e.printStackTrace();
				} catch (InterruptedException e) {

					e.printStackTrace();
				} catch (ExecutionException e) {

					e.printStackTrace();
				}
			}
		});

		/*
		 * cbSyscode = new JComboBox(new DataSourceModel());
		 * 
		 * DefaultFormBuilder idOptBuilder = new DefaultFormBuilder( new
		 * FormLayout("pref, 4dlu, fill:pref:grow")); idOptBuilder.append("Id:",
		 * txtId); idOptBuilder.append("System Code:", cbSyscode); JPanel idOpt
		 * = idOptBuilder.getPanel();
		 * 
		 * final JPanel opts = new JPanel(); final CardLayout optCards = new
		 * CardLayout(); opts.setLayout(optCards);
		 * 
		 * opts.add(idOpt, "ID");
		 * 
		 * JPanel searchOptBox2 = new JPanel(); FormLayout layout2 = new
		 * FormLayout( "4dlu, pref, 4dlu, fill:pref:grow, 4dlu, pref, 4dlu",
		 * "4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu"
		 * ); CellConstraints cc2 = new CellConstraints();
		 * 
		 * searchOptBox2.setLayout(layout2); Border etch =
		 * BorderFactory.createEtchedBorder();
		 * searchOptBox2.setBorder(BorderFactory.createTitledBorder(etch,
		 * "Search options"));
		 * 
		 * searchOptBox2.add(opts, cc.xyw(2, 4, 5));
		 */
		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				String strValue = shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);
			}
		});

		searchOptBox.add(clientDropdown, cc.xy(8, 1));
		// searchOptBox.add(tipLabel, cc.xyw(1, 3, 8));
		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);
		// searchOptBox.add(searchOptBox2, cc.xyw(1, 4,8));
		add(searchOptBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

		pTitleOrId.requestDefaultFocus();

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();
					SearchTableModel model = (SearchTableModel) target
							.getModel();

					File tmpDir = new File(plugin.getTmpDir(),
							shortClientName(model.clientName));
					tmpDir.mkdirs();

					try {
						plugin.openPathwayWithProgress(
								plugin.getClients().get(model.clientName),
								model.getValueAt(row, 0).toString(), 0, tmpDir);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(AdvancedSearchPanel.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	protected void searchPublicationRef() {

	}

	protected void doSearch() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = pTitleOrId.getText();
		String clientName = clientDropdown.getSelectedItem().toString();

		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);
		i = 0;
		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() {
			protected WSSearchResult[] doInBackground() throws Exception {
				pk.setTaskName("Searching");
				WSSearchResult[] results = null;
				ArrayList<WSSearchResult> results2 = new ArrayList<WSSearchResult>();
				try {

					// results = client.findPathwaysByXref(new
					// Xref(txtId.getText(), DataSource.getByFullName(""+
					// cbSyscode.getSelectedItem())));
					results = client.findPathwaysByXref(txtId.getText());

				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}

				for (WSSearchResult wsSearchResult : results) {
					if (wsSearchResult.getName().toUpperCase()
							.indexOf(query.toUpperCase()) != -1) {
						results2.add(wsSearchResult);
						i++;

					}
				}
				results = new WSSearchResult[i];
				results2.toArray(results);
				return results;

			}
		};

		sw.execute();
		d.setVisible(true);
		resultTable.setModel(new SearchTableModel2(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	public static String shortClientName(String clientName) {
		Pattern pattern = Pattern.compile("http://(.*?)/");
		Matcher matcher = pattern.matcher(clientName);

		if (matcher.find()) {
			clientName = matcher.group(1);
		}

		return clientName;
	}

	protected void browseByCurationTag() throws RemoteException,
			InterruptedException, ExecutionException {

		String clientName = clientDropdown.getSelectedItem().toString();
		final WikiPathwaysClient client = plugin.getClients().get(clientName);
		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);

		SwingWorker<WSCurationTag[], Void> sw = new SwingWorker<WSCurationTag[], Void>() {
			protected WSCurationTag[] doInBackground() throws Exception {

				pk.setTaskName("Searching");
				WSCurationTag[] results3 = null;
				ArrayList<WSCurationTag> results2 = new ArrayList<WSCurationTag>();
				int i = 0;
				String key = null;
				try {
					for (Entry<String, String> entry : curationtags.entrySet()) {
						if ((curationOpt.getSelectedItem().toString())
								.equals(entry.getValue())) {
							key = entry.getKey();
							break; // breaking because its one to one map
						}
					}

					results3 = client.getCurationTagsByName(key);

				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}

				return results3;
			}
		};

		sw.execute();
		d.setVisible(true);

		resultTable.setModel(new BrowseTableModel2(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	private void search() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = pTitleOrId.getText();
		String clientName = clientDropdown.getSelectedItem().toString();

		final WikiPathwaysClient client = plugin.getClients().get(clientName);

		final ProgressKeeper pk = new ProgressKeeper();
		final ProgressDialog d = new ProgressDialog(
				JOptionPane.getFrameForComponent(this), "", pk, true, true);
		i = 0;
		SwingWorker<WSSearchResult[], Void> sw = new SwingWorker<WSSearchResult[], Void>() {
			protected WSSearchResult[] doInBackground() throws Exception {
				pk.setTaskName("Searching");
				WSSearchResult[] results = null;
				ArrayList<WSSearchResult> results2 = new ArrayList<WSSearchResult>();
				try {

					results = client.findPathwaysByText(query);

				} catch (Exception e) {
					throw e;
				} finally {
					pk.finished();
				}

				for (WSSearchResult wsSearchResult : results) {
					if (wsSearchResult.getName().toUpperCase()
							.indexOf(query.toUpperCase()) != -1) {
						results2.add(wsSearchResult);
						i++;

					}
				}
				results = new WSSearchResult[i];
				results2.toArray(results);
				return results;

			}
		};

		sw.execute();
		d.setVisible(true);
		resultTable.setModel(new SearchTableModel2(sw.get(), clientName));
		resultTable.setRowSorter(new TableRowSorter(resultTable.getModel()));
	}

	private class SearchTableModel2 extends AbstractTableModel {
		WSSearchResult[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		String clientName;

		public SearchTableModel2(WSSearchResult[] results, String clientName) {
			this.clientName = clientName;
			this.results = results;
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			WSSearchResult r = results[rowIndex];
			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			}
			return "";
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

	private class BrowseTableModel2 extends AbstractTableModel {
		WSCurationTag[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species",
				"Curation Name" };
		String clientName;

		public BrowseTableModel2(WSCurationTag[] wsCurationTags,
				String clientName2) {
			this.clientName = clientName2;
			this.results = wsCurationTags;
		}

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			WSCurationTag r = results[rowIndex];

			switch (columnIndex) {
			case 0:
				return r.getPathway().getId();
			case 1:
				return r.getPathway().getName();
			case 2:
				return r.getPathway().getSpecies();
			case 3:
				return r.getDisplayName();
			}
			return "";
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

	private void searchID() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = pTitleOrId.getText();

		if (!query.isEmpty()) {
			String clientName = clientDropdown.getSelectedItem().toString();
			final WikiPathwaysClient client = plugin.getClients().get(
					clientName);
			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(
					JOptionPane.getFrameForComponent(this), "", pk, true, true);

			SwingWorker<WSPathwayInfo[], Void> sw = new SwingWorker<WSPathwayInfo[], Void>() {
				protected WSPathwayInfo[] doInBackground() throws Exception {
					i = 0;
					pk.setTaskName("Searching");

					WSPathwayInfo[] results3 = null;

					ArrayList<WSPathwayInfo> results2 = new ArrayList<WSPathwayInfo>();
					try {

						results2.add(client.getPathwayInfo(query));
						i++;

					} catch (Exception e) {
						throw e;
					} finally {
						pk.finished();
					}

					results3 = new WSPathwayInfo[i];
					results2.toArray(results3);
					return results3;

				}
			};

			sw.execute();
			d.setVisible(true);

			resultTable.setModel(new SearchTableModel(sw.get(), clientName));
			resultTable
					.setRowSorter(new TableRowSorter(resultTable.getModel()));
		} else {
			JOptionPane.showMessageDialog(null, "Please Enter a Search Query",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private class SearchTableModel extends AbstractTableModel {
		WSPathwayInfo[] results;
		String[] columnNames = new String[] { "ID", "Name", "Species" };
		String clientName;

		public SearchTableModel(WSPathwayInfo[] wsPathwayInfos,
				String clientName) {
			this.clientName = clientName;
			this.results = wsPathwayInfos;
		}

		public int getColumnCount() {
			return 3;
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			WSPathwayInfo r = results[rowIndex];
			switch (columnIndex) {
			case 0:
				return r.getId();
			case 1:
				return r.getName();
			case 2:
				return r.getSpecies();
			}
			return "";

		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

	}

}
