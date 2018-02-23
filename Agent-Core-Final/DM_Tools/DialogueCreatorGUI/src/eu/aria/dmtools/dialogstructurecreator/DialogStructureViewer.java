/* 
 * By Merijn Bruijnes, 
 * Human Media Interaction, University of Twente
 * for the ARIA VALUSPA project
 */

package eu.aria.dmtools.dialogstructurecreator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.mxgraph.examples.swing.GraphEditor;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.examples.swing.editor.JTableRenderer;
import com.mxgraph.examples.swing.editor.SchemaEditorMenuBar;
import com.mxgraph.examples.swing.editor.SchemaEditorToolBar;
import com.mxgraph.examples.swing.editor.SchemaGraphComponent;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxMultiplicity;
import com.mxgraph.view.mxStylesheet;
import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;


import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.json.simple.JSONArray;

import eu.aria.dmtools.dialogstructurecreator.DialogStructureData;

public class DialogStructureViewer extends BasicGraphEditor
{
	CMoveCreatorGUI cmovecreator = new CMoveCreatorGUI();
	DialogStructureData ds = new DialogStructureData();
	
	private static final long serialVersionUID = -7007225006753337933L;
	
//	private int edgeWidth = 60;
//	private int edgeHeight = 80;
//	private int vertexWidth = 140;
//	private int vertexHeight = 50;
//	private int edgeStrokeWidth = 3;
//	private int edgEendSize = 8;

	public DialogStructureViewer()
	{
		super("ARIA - Dialogue Structure Editor", new SchemaGraphComponent(new mxGraph()
		{
			/**
			 * Allows expanding tables
			 */
			public boolean isCellFoldable(Object cell, boolean collapse)
			{
				return model.isVertex(cell);
			}
		})

		{
			private static final long serialVersionUID = -1194463455177427496L;

			/**
			 * Disables folding icons.
			 */
			public ImageIcon getFoldingIcon(mxCellState state)
			{
				return null;
			}

		});
		
		// Creates a single shapes palette
		EditorPalette shapesPalette = insertPalette("Dialogue Elements");
		graphOutline.setVisible(false);
		
		//elements for validation... to fix
//		Document xmlDocument = mxDomUtils.createDocument();
////		Element episodeNode = xmlDocument.createElement("Episode");
//		String episodeNode = "Episode";
//		String exchangeNode = "Exchange";
//		String moveNode = "Move";
		
		mxGraph graph = getGraphComponent().getGraph();
		Object parent = graph.getDefaultParent();
		
		Episode myEpisode = new Episode();
		Exchange myExchange = new Exchange();
		Move myMove = new Move ();
		//myEpisode.setName("Episode");
		
		//define templates for palette
		mxCell episodeTemplate = new mxEpisodeCell(myEpisode, new mxGeometry(0, 0,
				160, 55), "Triple Rectangle");
		episodeTemplate.getGeometry().setAlternateBounds(
				new mxRectangle(0, 0, 100, 30));
		episodeTemplate.setVertex(true);
	
		
		mxCell exchangeTemplate = new mxExchangeCell(myExchange, new mxGeometry(0, 0,
				160, 55), "Double Rectangle");
		exchangeTemplate.getGeometry().setAlternateBounds(
				new mxRectangle(0, 0, 100, 30));
		exchangeTemplate.setVertex(true);
		
		mxCell moveTemplate = new mxMoveCell(myMove, new mxGeometry(0, 0,
				160, 110), "Rectangle");
		moveTemplate.getGeometry().setAlternateBounds(
				new mxRectangle(0, 0, 140, 30));
		moveTemplate.setVertex(true);
		
		//add it to the palette
		shapesPalette
			.addTemplate(
				"Episode",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/triplerectangle.png")),
				episodeTemplate);
		shapesPalette
		.addTemplate(
				"Exchange",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/doublerectangle.png")),
				exchangeTemplate);
		shapesPalette
		.addTemplate(
				"Move",
				new ImageIcon(
						GraphEditor.class
								.getResource("/com/mxgraph/examples/swing/images/rectangle.png")),
				moveTemplate);
		
		
		getGraphComponent().getGraph().setCellsResizable(false);
		getGraphComponent().setConnectable(true);
		getGraphComponent().getGraphHandler().setCloneEnabled(true);
		getGraphComponent().getGraphHandler().setImagePreview(true);
		getGraphComponent().getGraph().setCellsEditable(false);

		// Prefers default JComponent event-handling before mxCellHandler handling
		//getGraphComponent().getGraphHandler().setKeepOnTop(false);

		
		graph.getModel().beginUpdate();
		try
		{
//			//fancy vertex (with table content)
//			mxCell v1 = (mxCell) graph.insertVertex(parent, null, "Episode",
//					20, 20, 150, 80);
////			System.out.println("initially created as: "+v1.getValue());
////			v1.setValue("test");
////			System.out.println("initially changed to: "+v1.getValue());
////			v1.getGeometry().setAlternateBounds(new mxRectangle(0, 0, 140, 30));
//			
//			mxCell v2 = (mxCell) graph.insertVertex(parent, "test", exchangeNode,
//					20, 130, 150, 80);
//			v2.getGeometry().setAlternateBounds(new mxRectangle(0, 0, 140, 30));
//			
//			mxCell v3 = (mxCell) graph.insertVertex(parent, null, moveNode,
//					20, 240, 150, 80);
//			v3.getGeometry().setAlternateBounds(new mxRectangle(0, 0, 140, 30));
//			
//			graph.insertEdge(parent, null, "bla", v1, v2);
//			graph.insertEdge(parent, null, "blabla", v2, v3);
			
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		
		
		//final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		//getContentPane().add(graphComponent);
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			public void mouseReleased(MouseEvent e)
			{
				Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				mxCell mxcell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
				
				if (cell != null)
				{
					System.out.println("cell.label="+graph.getLabel(cell));
					System.out.println("value: "+ mxcell.getValue());
					
					if (mxcell instanceof mxMoveCell) {
						mxMoveCell mxMCell = (mxMoveCell) mxcell;
						Move move = mxMCell.getMove();
						System.out.println("Move: "+move.getName()+
								", UU: "+move.getUu()+
								", AU: "+move.getAu()+
								", rules: "+move.getRules()+
								", celltype: "+move.getCellType()
								);
					}
					if (mxcell instanceof mxExchangeCell) {
						mxExchangeCell mxexchangecell = (mxExchangeCell) mxcell;
						Exchange exchange = mxexchangecell.getExchange();
						System.out.println("Exchange: "+exchange.getName()
								);
					}
					if (mxcell instanceof mxEpisodeCell) {
						mxEpisodeCell mxepisodecell = (mxEpisodeCell) mxcell;
						Episode episode = mxepisodecell.getEpisode();
						System.out.println("Episode: "+episode.getName()
								);
					}
					
					graph.getModel().beginUpdate();
					try
					{
						String label = graph.getLabel(cell);
						
						////////////////////////////////////////// dingen voor structure
						
						//get the tree
//						Object[] edges;
//						edges = graph.getEdges(cell);
//						//graph.getDropTarget(edges, pt, mxcell);
//						Object[] nextvertex;
//						nextvertex = graph.getEdges(edges[0]);
//						System.out.println("edges: "+edges[0].toString()+", and nextvertex: "+nextvertex[0]);
//						
					}
					finally
					{
						graph.getModel().endUpdate();
					}
				}
			}
		});
		
		
		//stuff for validation
		mxMultiplicity[] multiplicities = new mxMultiplicity[5];

		// Episode nodes needs 1..600 connected Exchanges
		multiplicities[0] = new mxMultiplicity(true, "Episode", null, null, 1,
				"600", Arrays.asList(new String[] { "Exchange" }),
				"Episodes must have 1 or more Exchanges connected",
				"Episodes can only connect to Exchanges", true);

		// Episode node does not want any incoming connections
		multiplicities[1] = new mxMultiplicity(false, "Episode", null, null, 0,
				"0", null, "An Episode cannot have an incoming connection", null, true); // Type does not matter
		
		// Exchange nodes needs 1..600 connected Moves
				multiplicities[2] = new mxMultiplicity(true, "Exchange", null, null, 1,
						"600", Arrays.asList(new String[] { "Move" }),
						"Exchanges must have 1 or more Moves connected",
						"Exchanges can only connect to Moves", true);

		// Exchange needs exactly one incoming connection from Episode
		multiplicities[3] = new mxMultiplicity(false, "Exchange", null, null, 1,
				"1", Arrays.asList(new String[] { "Episode" }),
				"Exchanges must have 1 Episode", "Exchanges must connect from Episodes",
				true);
		
		// Move needs exactly one incoming connection from Exchange
				multiplicities[4] = new mxMultiplicity(false, "Move", null, null, 1,
						"1", Arrays.asList(new String[] { "Exchange" }),
						"Moves must have 1 Exchange", "Moves must connect from Exchanges",
						true);
		
		graph.setMultiplicities(multiplicities);
		
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graph.setMultigraph(false);
		graph.setAllowDanglingEdges(false);
		graphComponent.setConnectable(true);
		graphComponent.setToolTips(true);

		// Enables rubberband selection
		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
		
		graph.getModel().addListener(mxEvent.CHANGE, new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				graphComponent.validateGraph();
			}
		});
	}

	/**
	 * 
	 */
	protected void installToolBar()
	{
		add(new SchemaEditorToolBar(this, JToolBar.HORIZONTAL),
				BorderLayout.NORTH);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		DialogStructureViewer editor = new DialogStructureViewer();
		editor.createFrame(new SchemaEditorMenuBar(editor)).setVisible(true);
	}

}
