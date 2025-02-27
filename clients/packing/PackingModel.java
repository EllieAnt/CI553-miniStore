package clients.packing;


import catalogue.Basket;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockReadWriter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implements the Model of the warehouse packing client
 */
public class PackingModel extends Observable
{
  private AtomicReference<Basket> theBasket = new AtomicReference<>(); 

  private StockReadWriter theStock   = null;
  private OrderProcessing theOrder   = null;
  private String          theAction  = "";
  
  private StateOf         worker   = new StateOf();
  
  /*
  private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private PropertyChangeSupport pcl; // idk
  */

  /*
   * Construct the model of the warehouse Packing client
   * @param mf The factory to create the connection objects
   */
  public PackingModel(MiddleFactory mf)
  {
    try                                     // 
    {      
      theStock = mf.makeStockReadWriter();  // Database access
      theOrder = mf.makeOrderProcessing();  // Process order
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n%s", e.getMessage() );
    }

    theBasket.set( null );                  // Initial Basket
    // Start a background check to see when a new order can be packed
    new Thread( () -> checkForNewOrder() ).start();
  }
  

  
  
  /**
   * Semaphore used to only allow 1 order
   * to be packed at once by this person
   */
  class StateOf
  {
    private boolean held = false;
    
    /**
     * Claim exclusive access
     * @return true if claimed else false
     */
    public synchronized boolean claim()   // Semaphore
    {
      return held ? false : (held = true);
    }
    
    /**
     * Free the lock
     */
    public synchronized void free()     //
    {
      assert held;
      held = false;
    }

  }
  
  /**
   * Method run in a separate thread to check if there
   * is a new order waiting to be packed and we have
   * nothing to do.
   */
  private void checkForNewOrder()
  {
    while ( true )
    {
      try
      {
        boolean isFree = worker.claim();     // Are we free
        if ( isFree )                        // T
        {                                    //
          Basket sb = 
            theOrder.getOrderToPack();       //  Order 
          if ( sb != null )                  //  Order to pack
          {                                  //  T
            theBasket.set(sb);               //   Working on
            theAction = "Bought Receipt";     //   what to do
          } else {                           //  F
            worker.free();                   //  Free
            theAction = "";                  // 
          }
          setChanged(); notifyObservers(theAction);
          //pcs.firePropertyChange("checkForNewOrder", "", theAction); // theAct to sb?
        }                                    // 
        Thread.sleep(2000);                  // idle
      } catch ( Exception e )
      {
        DEBUG.error("%s\n%s",                // Eek!
           "BackGroundCheck.run()\n%s",
           e.getMessage() );
      }
    }
  }
  
  
  /**
   * Return the Basket of products that are to be picked
   * @return the basket
   */
  public Basket getBasket()
  {
    return theBasket.get();
  }
  /*
  public void addListener(PropertyChangeListener pcl) {
	  pcs.addPropertyChangeListener(pcl);
  }
  */

  /**
   * Process a packed Order
   */
  public void doPacked()
  {
    String theAction = "";
    try
    {
      Basket basket =  theBasket.get();       // Basket being packed
      if ( basket != null )                   // T
      {
        theBasket.set( null );                //  packed
        int no = basket.getOrderNum();        //  Order no
        theOrder.informOrderPacked( no );     //  Tell system
        theAction = "";                       //  Inform picker
        worker.free();                        //  Can pack some more
      } else {                                // F 
        theAction = "No order";       //   Not packed order
      }
      setChanged(); notifyObservers(theAction);
      //pcs.firePropertyChange("doPacked", "", theAction);
    }
    catch ( OrderException e )                // Error
    {                                         //  Of course
      DEBUG.error( "ReceiptModel.doOk()\n%s\n",//  should not
                            e.getMessage() ); //  happen
    }
    setChanged(); notifyObservers(theAction);
    //pcs.firePropertyChange("doPacked", "", theAction);
  }
}





