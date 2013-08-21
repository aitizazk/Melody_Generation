import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Random;

public class Melody_gen
{
    // BEVector is the symbol used to start or end a sequence.
	static Frame f= new Frame("Melody Learning");
    private static final double BE_VECTOR[] = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0,0.0};
   static TextField tf1=new TextField("Current Melody : ");
   static TextField tf2=new TextField("Generated Melody :");
   static Panel p=new Panel();
   static Button start1,start2,gen;
   static int london[]={7,8,7,6,5,6,7,0,4,5,6,0,5,6,7,0,7,8,7,6,5,6,7,0,4,7,5,3};
   static  int output[]=new int[17];
   static int x=0;
   																//   0    1    2    3    4    5
    private static final double SAMPLE_INPUT[][] = new double[][] {
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,0,0,1},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,1,0,0,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,1,0,0,0,0},
		{0,0,0,0,1,0,0,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,1,0,0,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,0,0,1},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,1,0,0,0},
		{0,0,0,0,0,1,0,0},
		{0,0,0,0,0,0,1,0},
		{0,0,0,0,0,0,0,0},
		{0,0,0,1,0,0,0,0},
		{0,0,0,0,0,0,1,0},
		{0,0,1,0,0,0,0,0}};

    private static final int MAX_TESTS = 10000;

    private static final int MAX_SAMPLES = 27;

    private static final int INPUT_NEURONS = 8;
    private static final int HIDDEN_NEURONS = 4;
    private static final int OUTPUT_NEURONS = 8;
    private static final int CONTEXT_NEURONS = 4;

    private static final double LEARN_RATE = 0.15;    // Rho.
    private static final int TRAINING_REPS = 200000;

    // Input to Hidden Weights (with Biases).
    private static double wih[][] = new double[INPUT_NEURONS + 1][HIDDEN_NEURONS];

    // Context to Hidden Weight (with Biases).
    private static double wch[][] = new double[CONTEXT_NEURONS + 1][HIDDEN_NEURONS];

    // Hidden to Output Weights (with Biases).
    private static double who[][] = new double[HIDDEN_NEURONS + 1][OUTPUT_NEURONS];

    // Hidden to Context Weights (no Biases).
    private static double whc[][] = new double[OUTPUT_NEURONS + 1][CONTEXT_NEURONS];
                                                                   
    // Activations.
    private static double inputs[] = new double[INPUT_NEURONS];
    private static double hidden[] = new double[HIDDEN_NEURONS];
    private static double target[] = new double[OUTPUT_NEURONS];
    private static double actual[] = new double[OUTPUT_NEURONS];
    private static double context[] = new double[CONTEXT_NEURONS];

    // Unit errors.
    private static double erro[] = new double[OUTPUT_NEURONS];
    private static double errh[] = new double[HIDDEN_NEURONS];

    private static void elmanNetwork()
    {
    	
        double err = 0.0;
        int sample = 0;
        int iterations = 0;
        boolean stopLoop = false;

        assignRandomWeights();

        // Train the network.
        while(!stopLoop)
        {
        	
            if(sample == 0){
                for(int i = 0; i < INPUT_NEURONS; i++)
                {
                    inputs[i] = BE_VECTOR[i];
                }
            }else{
                for(int i = 0; i < INPUT_NEURONS; i++)
                {
                    inputs[i] = SAMPLE_INPUT[sample - 1][i];
                }
            }

            // After the samples are entered into the input units, the sample are
            // then offset by one and entered into target-output units for
            // later comparison.
            if(sample == MAX_SAMPLES - 1){
                for(int i = 0; i < INPUT_NEURONS; i++)
                {
                    target[i] = BE_VECTOR[i];
                }
            }else{
                for(int i = 0; i < INPUT_NEURONS; i++)
                {
                    target[i] = SAMPLE_INPUT[sample][i];
                }
            }

            feedForward();

            err = 0.0;
            for(int i = 0; i < OUTPUT_NEURONS; i++)
            {
                err += Math.sqrt(target[i] - actual[i]);
            }
            err = 0.5 * err;

            if(iterations > TRAINING_REPS){
                stopLoop = true;
            }
            iterations++;

            backPropagate();

            sample++;
            if(sample == MAX_SAMPLES){
                sample = 0;
            }
        }

        System.out.println("Iterations = " + iterations);
        return;
    }

    private static void testNetwork()
    {
    	
        int index = 0;
        int randomNumber = 0;
        int predicted = 0;
        boolean stopSample = false;
        boolean successful = false;
        DecimalFormat dfm = new java.text.DecimalFormat("###0.000");

        // Test the network with random input patterns.
        for(int test = 0; test < MAX_TESTS; test++) // Do random tests.
        {
        	if(x==16){break;}
            // Enter Beginning string.
            inputs[0] = 0.0;
            inputs[1] = 0.0;
            inputs[2] = 0.0;
            inputs[3] = 0.0;
            inputs[4] = 0.0;
            inputs[5] = 0.0;
            inputs[6] = 1.0;
            inputs[7] = 0.0;
            
            System.out.print("\n(0) ");

            feedForward();

            stopSample = false;
            successful = false;
            index = 0;
            randomNumber = 0;
            predicted = 0;
            while(stopSample == false)
            {
            	
                for(int i = 0; i < OUTPUT_NEURONS; i++)
                {
                    System.out.print(dfm.format(actual[i]) + " ");
                    if(actual[i] > 0.0){
                       
                    	if(actual[i]>actual[predicted]){
                    	predicted = i;}
                    }
                } // i
                
                System.out.print("\n");

                index++;
                if(index == OUTPUT_NEURONS - 1){
                    stopSample = true;
                }

              
                randomNumber = predicted;
                output[x]=predicted;
                
                if(x<16){x++;}
                System.out.print("(" + randomNumber + ") ");
                for(int i = 0; i < INPUT_NEURONS; i++)
                {
                    if(i == randomNumber){
                        inputs[i] = 1.0;
                        if(i == predicted){
                            successful = true;
                        }else{
                            // failure. Stop this sample and try a new sample.
                            stopSample = true;
                        }
                    }else{
                        inputs[i] = 0.0;
                    }
                } // i

                feedForward();

            } 
            
            if((index >16 /*OUTPUT_NEURONS - 2*/) && (successful == true)){
            	// if((index >1) && (successful == true)){
                // If the random sequence happens to be in the correct order, the network reports success.
                System.out.println("Success.");
                System.out.println("Completed " + test + " tests.");
                break;
            }/*else{
                System.out.println("Failed.");
                if(test > MAX_TESTS){
                    System.out.println("Completed " + test + " tests with no success.");
                    break;
                }
            }*/
        } // test
   //     PlaySoundApplet psa=new PlaySoundApplet();
   // 	psa.init();
        return;
    }

    private static void feedForward()
    {
        double sum = 0.0;

        // Calculate input and context connections to hidden layer.
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            sum = 0.0;
            for(int inp = 0; inp < INPUT_NEURONS; inp++)    // from input to hidden...
            {
                sum += inputs[inp] * wih[inp][hid];
            } // inp
            
            for(int con = 0; con < CONTEXT_NEURONS; con++)    // from context to hidden...
            {
                sum += context[con] * wch[con][hid];
            } // con
            
            sum += wih[INPUT_NEURONS][hid];    // Add in bias.
            sum += wch[CONTEXT_NEURONS][hid];
            hidden[hid] = sigmoid(sum);
        } // hid

        // Calculate the hidden to output layer.
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            sum = 0.0;
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                sum += hidden[hid] * who[hid][out];
            } // hid

            sum += who[HIDDEN_NEURONS][out];    // Add in bias.
            actual[out] = sigmoid(sum);
        } // out

        // Copy outputs of the hidden to context layer.
        for(int con = 0; con < CONTEXT_NEURONS; con++)
        {
            context[con] = hidden[con];
        }
        return;
    }

    private static void backPropagate()
    {
        // Calculate the output layer error 
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            erro[out] = (target[out] - actual[out]) * sigmoidDerivative(actual[out]);
        }

        // Calculate the hidden layer error
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            errh[hid] = 0.0;
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                errh[hid] += erro[out] * who[hid][out];
            } 
            errh[hid] *= sigmoidDerivative(hidden[hid]);
        } 

        // Update the weights for the output layer 
        for(int out = 0; out < OUTPUT_NEURONS; out++)
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                who[hid][out] += (LEARN_RATE * erro[out] * hidden[hid]);
            } 
            
            who[HIDDEN_NEURONS][out] += (LEARN_RATE * erro[out]);    // Update the bias.
        } 

        // Update the weights for the hidden layer .
        for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
        {
            for(int inp = 0; inp < INPUT_NEURONS; inp++)
            {
                wih[inp][hid] += (LEARN_RATE * errh[hid] * inputs[inp]);
            } 
            
            wih[INPUT_NEURONS][hid] += (LEARN_RATE * errh[hid]);    // Update the bias.
        } 
        
        return;
    }

    private static void assignRandomWeights()
    {
        for(int inp = 0; inp <= INPUT_NEURONS; inp++)    
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                // Assign a random weight value between -0.5 and 0.5
                wih[inp][hid] = new Random().nextDouble() - 0.5;
            } 
        } 

        for(int con = 0; con <= CONTEXT_NEURONS; con++)
        {
            for(int hid = 0; hid < HIDDEN_NEURONS; hid++)
            {
                // Assign a random weight value between -0.5 and 0.5
                wch[con][hid] = new Random().nextDouble() - 0.5;
            } 
        } 

        for(int hid = 0; hid <= HIDDEN_NEURONS; hid++)    //Do not subtract 1 here.
        {
            for(int out = 0; out < OUTPUT_NEURONS; out++)
            {
                // Assign a random weight value between -0.5 and 0.5
                who[hid][out] = new Random().nextDouble() - 0.5;
            } 
        } 

        for(int out = 0; out <= OUTPUT_NEURONS; out++)
        {
            for(int con = 0; con < CONTEXT_NEURONS; con++)
            {
                // all fixed weights set to 0.5
                whc[out][con] = 0.5;
            } 
        } 
        return;
    }

  

    private static double sigmoid(double val)
    {
        return (1.0 / (1.0 + Math.exp(-val)));
    }

    private static double sigmoidDerivative(double val)
    {
        return (val * (1.0 - val));
    }
   
    
    
    
    
    public static void main(String[] args)
    
    { 
    	tf1.setEditable(false);
    	tf2.setEditable(false);
    	f. addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
        }
    });

    	f.setLayout(new GridLayout(2,2));
    	f.setSize(500,300);
    	
    	f.setVisible(true);
    	 final AudioClip audioClip;
    	try
        {  
            String filePath = "F:\\workspace\\test\\1.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip2;
    	try
        {  
            String filePath = "F:\\workspace\\test\\2.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip2 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip3;
    	try
        {  
            String filePath = "F:\\workspace\\test\\3.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip3 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip4;
    	try
        {  
            String filePath = "F:\\workspace\\test\\4.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip4 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip5;
    	try
        {  
            String filePath = "F:\\workspace\\test\\5.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip5 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	final AudioClip audioClip6;
    	try
        {  
            String filePath = "F:\\workspace\\test\\6.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip6 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip7;
    	try
        {  
            String filePath = "F:\\workspace\\test\\7.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip7 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	
    	final AudioClip audioClip8;
    	try
        {  
            String filePath = "F:\\workspace\\test\\8.wav";

            File file = new File(filePath);

            if (file.exists())
            {
                audioClip8 = Applet.newAudioClip(file.toURI().toURL());
            }
            else
            {
                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
            }
        }
        catch (MalformedURLException malformedURLException)
        {
            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
        }
    	
    	start1=new Button("Play Current Melody");
    	start2=new Button("Play Generated Melody");
    	gen=new Button("Train & Generate");
    	 start1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				 Button source = (Button)ae.getSource();

			      if (source.getLabel() == "Play Current Melody")
			      {
			    	  tf1.setText("Current Melody :");
			    	  AudioClip temp;
			      	try
			        {  
			            String filePath = "F:\\workspace\\test\\8.wav";

			            File file = new File(filePath);

			            if (file.exists())
			            {
			                temp = Applet.newAudioClip(file.toURI().toURL());
			            }
			            else
			            {
			                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
			            }
			        }
			        catch (MalformedURLException malformedURLException)
			        {
			            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
			        }
			    	  for(int i=0;i<london.length;i++){
			    		 if(london[i]==1){temp=audioClip;temp.play();}
			    		  if(london[i]==2){temp=audioClip2;temp.play();}
			    		  if(london[i]==3){temp=audioClip3;temp.play();}
			    		  if(london[i]==4){temp=audioClip4;temp.play();}
			    		  if(london[i]==5){temp=audioClip5;temp.play();}
			    		  if(london[i]==6){temp=audioClip6;temp.play();}
			    		  if(london[i]==7){temp=audioClip7;temp.play();}
			    		  if(london[i]==8){temp=audioClip8;temp.play();}
			          
			    		  tf1.setText(tf1.getText()+london[i]);
			    		 
			    		  
			          try {
						Thread.sleep(550);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         // temp.stop();
			         // System.out.println("Play was executed");
			          }
			      }

			}
    		 
    	 });
    	 start2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				 Button source = (Button)ae.getSource();

				if(source.getLabel() == "Play Generated Melody")
			      {AudioClip temp;
			      tf2.setText("Generated Melody :");
			      	try
			        {  
			            String filePath = "F:\\workspace\\test\\8.wav";

			            File file = new File(filePath);

			            if (file.exists())
			            {
			                temp = Applet.newAudioClip(file.toURI().toURL());
			            }
			            else
			            {
			                throw new RuntimeException("Directory " + filePath + " does not exist"); //debugging
			            }
			        }
			        catch (MalformedURLException malformedURLException)
			        {
			            throw new RuntimeException("Malformed URL: " + malformedURLException);  //debugging
			        }
			      	
					for(int i=0;i<output.length;i++){
			    		
			          
			    		 if(output[i]==0){temp=audioClip;temp.play();}
			    		  if(output[i]==1){temp=audioClip2;temp.play();}
			    		  if(output[i]==2){temp=audioClip3;temp.play();}
			    		  if(output[i]==3){temp=audioClip4;temp.play();}
			    		  if(output[i]==4){temp=audioClip5;temp.play();}
			    		  if(output[i]==5){temp=audioClip6;temp.play();}
			    		  if(output[i]==6){temp=audioClip7;temp.play();}
			    		  if(output[i]==7){temp=audioClip8;temp.play();}
			    tf2.setText(tf2.getText()+(output[i]+1));
			    		 
			    		  
			          try {
						Thread.sleep(550);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			         
			         // System.out.println("Play was executed");
			          }
			      }
			      }
			
    		 
    		 
    		 
    	 });
    	 gen.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				elmanNetwork();
		        testNetwork();
			}});
  f.add(start1);
  p.setLayout(new GridLayout(2,1));
  
  p.add(gen);
  p.add(start2);
  f.add(p);
  f.add(tf1);
  f.add(tf2);
    	
        
        	
       
        return;
    }


	

}