package clients.packing;

import catalogue.Basket;
import clients.customer.CustomerModel;
import middle.MiddleFactory;
import middle.OrderProcessing;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Implements the Packing view.

 */

public class PackingView implements Observer
//public class PackingView implements PropertyChangeListener
{
  private static final String PACKED = "Packed";

  private static final int H = 375;       // Height of window pixels
  private static final int W = 500;       // Width  of window pixels

  private final JLabel      pageTitle  = new JLabel();
  private final JLabel      theAction  = new JLabel();
  private final JTextArea   theOutput  = new JTextArea();
  private final JScrollPane theSP      = new JScrollPane();
  private final JButton     theBtPack= new JButton( PACKED );
  private CustomerModel model;
 
  private OrderProcessing theOrder     = null;
  
  private PackingController cont= null;

  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-cordinate of position of window on screen 
   * @param y     y-cordinate of position of window on screen  
   */
  public PackingView(  RootPaneContainer rpc, MiddleFactory mf, int x, int y )
  {
    try                                           // 
    {      
      theOrder = mf.makeOrderProcessing();        // Process order
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( (x), (y+80) );          // location of window
    cp.setBackground(new Color(37, 113, 128));      // Colour (Teal)
    
    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
    
    pageTitle.setBounds( 110, 0 , 270, 20 );       
    pageTitle.setText( "Packing Bought Order" );                        
    cp.add( pageTitle );

    theBtPack.setBounds( 16, 25+60*0, 80, 40 );   // Check Button
    theBtPack.addActionListener(                   // Call back code
      e -> cont.doPacked() );
    cp.add( theBtPack );                          //  Add to canvas
    theBtPack.setBackground(new Color(253, 139, 81)); // Colour (Orange)
    
    theAction.setBounds( 110, 25 , 270, 20 );       // Message area
    theAction.setText( "" );                        // Blank
    cp.add( theAction );                            //  Add to canvas

    theSP.setBounds( 110, 55, 270, 205 );           // Scrolling pane
    theOutput.setText( "" );                        //  Blank
    theOutput.setFont( f );                         //  Uses font
    theOutput.setBackground(new Color(242, 229, 191)); // Colour (Beige)
    cp.add( theSP );                                //  Add to canvas
    theSP.getViewport().add( theOutput );           //  In TextArea
    rootWindow.setVisible( true );                  // Make visible
  }
  
  public void setController( PackingController c )
  {
    cont = c;
  }

  public void setModel( CustomerModel m )
  {
    model = m;
  }
  
  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  
  @Override
  public void update( Observable modelC, Object arg )
  {
	  PackingModel model  = (PackingModel) modelC;
    String        message = (String) arg;
    theAction.setText( message );
    
    Basket basket =  model.getBasket();
    if ( basket != null )
    {
      theOutput.setText( basket.getDetails() );
    } else {
      theOutput.setText("");
    }
  }

  /*
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
	  String proName = evt.getPropertyName();
	  String oldValue = (String) evt.getOldValue();
	  String newValue = (String) evt.getNewValue();
	  theAction.setText(newValue);
	  switch(proName) {
	  case "doPacked":
		  Basket basket =  model.getBasket();
		  if ( basket != null )
		  {
		    theOutput.setText( basket.getDetails() );
		  } else {
		    theOutput.setText("");
		  }
		  break;
  }
 }
  */
}

