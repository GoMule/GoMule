package gomule.gui.stashfilter;

import gomule.item.filter.*;
import gomule.item.filter.free.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import randall.util.*;

public class D2FilterSingleFreeHandler
{
	private D2FilterHandler			iFilterHandler;
	private ArrayList				iFilterFactoryList;
	// private D2ItemFilterQueryFactory iFilterFactory;

	private RandallPanel			iPanel;

	private JComboBox				iSelectionComboBox;
	private DefaultComboBoxModel	iSelectionModel;
	
	private JComboBox				iOperatorComboBox;
	private DefaultComboBoxModel	iOperatorModel;
	
	private JTextField				iValueField;
	
	private D2ItemFilter			iItemFilter;

	public D2FilterSingleFreeHandler( D2FilterHandler pFilterHandler, ArrayList pFilterFactoryList )
	{
		iFilterHandler = pFilterHandler;
		iFilterFactoryList = pFilterFactoryList;
		
		iPanel = new RandallPanel();

		iSelectionModel = new DefaultComboBoxModel();
		for ( int i = 0; i < pFilterFactoryList.size(); i++ )
		{
			// mbr: force cast as little check to know all items in model are
			// valid
			D2ItemFilterQueryFactory lFactory = (D2ItemFilterQueryFactory) pFilterFactoryList.get( i );
			iSelectionModel.addElement( lFactory );
		}
		iSelectionComboBox = new JComboBox(iSelectionModel);
		iPanel.addToPanel( iSelectionComboBox, 0, 0, 1, RandallPanel.HORIZONTAL );
		
		iOperatorModel = new DefaultComboBoxModel();
		iOperatorComboBox = new JComboBox(iOperatorModel);
		iPanel.addToPanel( iOperatorComboBox, 1, 0, 1, RandallPanel.HORIZONTAL );
		
		iValueField = new JTextField();
		Dimension lSize = new Dimension(50,25);
		iValueField.setMinimumSize( lSize );
		iValueField.setPreferredSize( lSize );
		iPanel.addToPanel( iValueField, 2, 0, 1, RandallPanel.NONE );
		
		iSelectionComboBox.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent pE )
			{
				refreshOperatorBox();
			}
		});
		
		iOperatorComboBox.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent pE )
			{
				checkFilter();
			}
		});
		
		iValueField.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent pE )
			{
				checkFilter();
			}
		});
		iValueField.getDocument().addDocumentListener( new DocumentListener(){

			public void changedUpdate( DocumentEvent pE )
			{
				checkFilter();
			}

			public void insertUpdate( DocumentEvent pE )
			{
				checkFilter();
			}

			public void removeUpdate( DocumentEvent pE )
			{
				checkFilter();
			}
			
		});
		
		refreshOperatorBox();
	}
	
	/**
	 * Marco: not very nice, but for now easy enough to work with
	 * @param pIndex
	 */
	public void setSelectedIndex( int pIndex )
	{
		iSelectionComboBox.setSelectedIndex( pIndex );
	}
	
	/**
	 * Marco: not very nice, but for now easy enough to work with
	 * @param pIndex
	 */
	public void setSelectedOp( int pIndex )
	{
		iOperatorComboBox.setSelectedIndex( pIndex );
	}
	
	protected void refreshOperatorBox()
	{
//		System.err.println("refreshOperatorBox()");
		iOperatorModel.removeAllElements();
		D2ItemFilterQueryFactory lFactory = (D2ItemFilterQueryFactory) iSelectionComboBox.getSelectedItem();
		ArrayList lOperatorList = lFactory.getOperator();
		for ( int i = 0 ; i < lOperatorList.size() ; i++ )
		{
			D2ItemFilterQueryOperator lOperator = (D2ItemFilterQueryOperator) lOperatorList.get( i );
			iOperatorModel.addElement( lOperator );
		}
		checkFilter();
//		iValueField.setText( "" );
	}
	
	protected void checkFilter()
	{
//		System.err.println("checkFilter()");
		// first remove old filter
		if ( iItemFilter != null )
		{
			iFilterHandler.removeFilter( D2ViewStashFilterNew.TEXT_FILTER, iItemFilter );
			iItemFilter = null;
		}
		
		String lValue = iValueField.getText();
		
		D2ItemFilterQueryFactory lFactory = (D2ItemFilterQueryFactory) iSelectionComboBox.getSelectedItem();
		D2ItemFilterQueryOperator lOperator = (D2ItemFilterQueryOperator) iOperatorComboBox.getSelectedItem();
		
		if ( lValue != null && lFactory != null && lOperator != null )
		{
			Object lRealValue = lFactory.getDataValue( lValue );
			if ( lRealValue != null )
			{
				iItemFilter = lFactory.getFilter( lOperator, lRealValue );
				iFilterHandler.addFilter( D2ViewStashFilterNew.TEXT_FILTER, iItemFilter );
			}
		}
	}

	public JComponent getDisplay()
	{
		return iPanel;
	}
}
