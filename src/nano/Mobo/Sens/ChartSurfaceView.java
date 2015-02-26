package nano.Mobo.Sens;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class ChartSurfaceView extends SurfaceView implements Callback {
	  Paint paint = new Paint();
	  private short [] PCMsamples = null;
	 private int numOfShorts;
	 private File f = null;
	 private FileOutputStream fo = null;
	// Canvas myCanvas;
	 double recordSamplingRate;
	 float tempX = 0;
	 float tempY = 0;
	 float Oldx = 0;
	 float Oldy = 0;
	 
	 private 	  byte [] BBB = null;
	 private boolean stopped = false;
	 Vector<Position> validDots = null;
	private SimpleDateFormat sdf = null;
	 private String currentDateandTime = null;
	 private boolean shouldclear = false;
	 public void clearSDFandTime()
	 {
		 sdf = null;
		 currentDateandTime = null;
		 
		 
	 }
	 
	 public void clearFileStream()
	 {
		 
		shouldclear = true;
		
		 
	 }
	 
	 public void setStopped(){
		 
		 stopped = true;
		 
	 }
	 
	 public void setResumed(){
		 
		 resumed = true;
		 
	 }
	 private boolean resumed = false;
		private void writeToFile(short[] array)  {
			
	//		Log.i("Entering writeToFile...:", "");
			// TODO Auto-generated method stub
			
			if(resumed==true&&stopped==true)
				return;
			
			if(sdf == null || currentDateandTime == null){
			 sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			 currentDateandTime = sdf.format(new Date());
			}
			f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "MoboSensRawData"+currentDateandTime+".txt");
			  if(!f.exists())
			  {
				  try {
					f.createNewFile();
					fo = new FileOutputStream(f, false);
				} catch (IOException e) {
					fo = null;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
			  }
			  else{
				  
				  //fo = null;
				  try{
					fo = new FileOutputStream(f, true);
				} catch (FileNotFoundException e1) {
					fo = null;
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  
				
				}

			  
		
			  
			  
			  for(int i=0;i<array.length;i++){
				  
				  if(shouldclear==true)
				  {
					  
					  fo = null;
					  System.gc();
					  shouldclear = false;
					  return;
					  
				  }
				  
				  
				  String str = Short.toString(array[i]);
				  
			//	  Log.i(str, str);
				  BBB = new byte[str.length()];
				  for(int j=0;j<str.length();j++)
				  {
					  
					  BBB[j] = (byte) str.charAt(j);
					  
				  }
				  
			  try {
				 
				fo.write(BBB);
				fo.write(' ');
			} catch (IOException e) {
				fo = null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  }
			  // remember close de FileOutput
			  try {
				fo.close();
				fo = null;
			} catch (IOException e) {
				
				fo = null;
				// TODO Auto-generated catch block
				e.printStackTrace();  
			//	BBB = null;
				  
			  }
			  
			  
			        
			    
		}
		
		private void clearChartSurfaceView()
		{
			  tempX = 0;
			  tempY = 0;
			  Oldx = 0;
			  Oldy = 0;
			validDots = null;
			PCMsamples = null;
			  sdf = null;
			  BBB = null;
			  //BBB = null;
			   currentDateandTime = null;
		}
	 
	 public void copyPCMsamples(short [] PCMbuffer) 
	 {
		 
		// Log.i("Entering copyPCMsamples():","");
	
		  
		 

		PCMsamples = new short [PCMbuffer.length];
		System.arraycopy( PCMbuffer, 0, PCMsamples, 0, PCMbuffer.length );
		//writeToFile(PCMsamples);
		 
	 }
	 
	public void addPCMsamples(short [] PCMbuffer)
	{
		//this function takes in a PCM buffer and add the samples 
		
		
	}
	public void initialize(int numShorts, double recordSampling){
		recordSamplingRate = recordSampling;
		numOfShorts = numShorts;
		//param numShorts, how many samples here
		PCMsamples = new short[numShorts];
	}
	
	private void resizePCMArray(int NewNumShorts){
		
		short [] temp = new short[NewNumShorts];
		
		int i = 0;
		for(i=0;i<PCMsamples.length;i++)
		{
			temp[i] = PCMsamples[i];
			
			
		}
		
		PCMsamples = temp;
		
		
	}
	
	public boolean isUninitilzied()
	{
		if(PCMsamples == null)
			return true;
		
		return false;
		
		
	}
	
	public boolean IfPCMEmpty()
	{
		
		if( PCMsamples.length == 0)
			return true;
		
		return false;
		
	}
	
	private short convertAmplitudeVal(short rawAmp)
	{
		//unimplemented method, pixelize amplitude
		return -1;
		
	}
	
	
	
	public void zoomIn()
	{
		
		
		
	}
	
	public void zoomOut()
	{
		
		
		
		
	}
	
	public void setDot(float xcor, float ycor){
		
    //	Canvas c = getHolder().lockCanvas();
	//	c.drawLine(Oldx, Oldy, xcor, ycor, paint);
		
		//this.draw(myCanvas);
//	   getHolder().unlockCanvasAndPost(c);
   if(validDots == null)
   {
	   validDots = new Vector<Position>();
	   
   }
   
 
		validDots.add(new Position(xcor, ycor));
		

	//	 tempX = xcor;
	//	  tempY = ycor;
	}
   
    @Override
    public void onDraw(Canvas canvas) {
    

    	//if(sdf == null)
    	//	return;
    	
    	if(PCMsamples==null)
    		return;
    	int wid = getWidth();
    	
    	 int hei = getHeight();

		 paint.setColor(Color.RED);
	        paint.setStrokeWidth(1);

	     int len = wid;
         if(PCMsamples.length < wid)
         {
        	 len = PCMsamples.length;
        	 
         }
	     
	     if(PCMsamples.length == 0)
	    	 return;

	     
	     float  ex, nx;
	     float  ey, ny;
	     for(int i = 0;i<len;i++)
	     {
	    	 ex = i;
	    	 ey = hei/2;
	    	 
	    	 nx = i;
 	    	 ny = -PCMsamples[i]*ey/Short.MAX_VALUE+ey;
	    	 canvas.drawLine(ex, ey, nx, ny, paint);
	    //	 Log.i("drawing index: ", Short.toString(PCMsamples[(int) i]));
	    	// Log.i("drawing Amp: ", Float.toString(ey));
	    	 
	     }
	     // Oldx = tempX;
		  //  Oldy = tempY;
	     super.onDraw(canvas);
    }
    
    public void clearBBB()
    {
    	
    	
    		BBB = null;
    	
    	
    }
	public ChartSurfaceView(Context context, AttributeSet attributeSet) {
		  
		  super(context, attributeSet);
		  this.setBackgroundColor(Color.GRAY);
		// 
		//  clearChartSurfaceView();
		    getHolder().addCallback(this);
		    //final int wid = this.getWidth();
		  //  validDots = new Vector<Position>();
		   // validDots.add(new Position(0,this.getHeight()/2));
		   
		  
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {

		//Canvas c = getHolder().lockCanvas();
		
	//	Paint myPaint = new Paint();
		//myPaint.setColor(Color.GREEN);
		//myPaint.setStrokeWidth(10);
		//c.drawRect(100, 100, 200, 200, myPaint);
	//	onDraw(c);
		// drawDot(20, 20);
	 //   getHolder().unlockCanvasAndPost(c);
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
