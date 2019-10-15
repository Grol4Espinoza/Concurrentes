package redesOk;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import redesOk.TCPClient50;

class Cliente51 {

    public int sum[] = new int[40];
    public int lista_de_imagenes[][][] = new int[1000][][];  
    
    String[] pathnames;
    TCPClient50 mTcpClient;
    Scanner sc;
    LoadImage imagenes;
	
    public static void main(String[] args) {
        Cliente51 objcli = new Cliente51();
        objcli.iniciar();
    }

    void iniciar() {
        new Thread(
                new Runnable() {

            @Override
            public void run() {
                mTcpClient = new TCPClient50("127.0.0.1",
                        new TCPClient50.OnMessageReceived() {
                    @Override
                    public void messageReceived(String message) {
                        ClienteRecibe(message);
                    }
                }
                );
                mTcpClient.run();
            }
        }
        ).start();
        //---------------------------

        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Cliente bandera 01");
        while (!salir.equals("s")) {
            salir = sc.nextLine();
            ClienteEnvia(salir);
        }
        System.out.println("Cliente bandera 02");

    }

    void ClienteRecibe(String llego) {
        System.out.println("CLINTE50 El mensaje::" + llego);
        if (llego.trim().contains("evalua")) {
            String arrayString[] = llego.split("\\s+");
            int min = Integer.parseInt(arrayString[1]);
            int max = Integer.parseInt(arrayString[2]);

            System.out.println("el min:" + min + " el max:" + max);
            procesar(min, max);
        }
    }

    void ClienteEnvia(String envia) {
        if (mTcpClient != null) {
            mTcpClient.sendMessage(envia);
        }
    }

   
    void procesar(int a, int b) {
        int N = (b - a);//14;
        int H = 12;//luego aumentar
        int d = (int) ((N) / H);
        Thread todos[] = new Thread[200];
        for (int i = 0; i < (H - 1); i++) { 
            todos[i] = new tarea0101((i * d + a), (i * d + d + a), i);
            todos[i].start();
        }
        
        todos[H - 1] = new tarea0101(((d * (H - 1)) + a), (b + 1), H - 1);
        todos[H - 1].start();
        for (int i = 0; i <= (H - 1); i++) {//AQUI AQUI VER <=
            try {
                todos[i].join();
            } catch (InterruptedException ex) {
                System.out.println("error" + ex);
            }
        }
        
             
        
        
        //CONVOLUSION 
        
        int nro_filas = 0 ;
        int nro_columnas = 0 ;        
        Thread hilos[][] = new Thread[1000][1000];
        int largo_de_lista = pathnames.length;
        //BufferedImage[] outputlists = new BufferedImage[100];           
        for (int n = 0; n <= largo_de_lista; n++) { //recorre la lista de imagenes        	
        	if(a <=n && n <= b) {
        		int [][] original_image = lista_de_imagenes[n];        		
	        	nro_filas = lista_de_imagenes[n].length;
	        	nro_columnas = lista_de_imagenes[n][0].length;
	        	//outputlists[n] = new BufferedImage(nro_filas, nro_columnas, BufferedImage.TYPE_INT_RGB);
        		BufferedImage outputlists = null;
				try {
					outputlists = ImageIO.read(new File("C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[n-1]+"_converted.jpg"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	        	System.out.println("OPERANDO IMAGEN: "+n);   
		        for (int x = 0; x < nro_filas; x++) { //recorre las filas de la imagen
		        	for (int y = 0; y < nro_columnas; y++){ //recorre los pixeles de cada fila	
		        		//System.out.println("operando imagen: "+n+" hilo["+x+"]["+y+"]"); 
		        		hilos[x][y] = new convolucion(n, x, y, nro_filas, nro_columnas, original_image,outputlists);
		        		hilos[x][y].start();
		        	}		        	
		        }
		        //System.out.println("FINALIZADA IMAGEN: "+n);
        	}
	        	        
	    }
        
        System.out.println("UNIENDO HILOS.... ");
                
        for (int n = 0; n < largo_de_lista; n++) { //recorre la lista de imagenes
        	if(a <=n && n <= b) {         		
        		nro_filas = lista_de_imagenes[n].length;
	        	nro_columnas = lista_de_imagenes[n][1].length;  	        	
	        	System.out.println("JOIN IMAGEN: "+n); 
		        for (int x = 0; x < nro_filas; x++) { //recorre las filas de la imagen
		        	for (int y = 0; y < nro_columnas; y++){ //recorre los pixeles de cada fila	        		
		        		 try {
		        			 //System.out.println("uniendo imagen: "+n+" hilo["+x+"]["+y+"]"); 
		                     hilos[x][y].join();
		                 } catch (InterruptedException ex) {
		                     System.out.println("error" + ex);
		                   }
		        	}
		        }		            
        	}         	
        }
        
        
        int n = largo_de_lista;
        if(a <=n && n <= b) {         		
    		nro_filas = lista_de_imagenes[n].length;
        	nro_columnas = lista_de_imagenes[n][1].length;  
        	System.out.println("JOIN IMAGEN: "+n); 
	        for (int x = 0; x < nro_filas; x++) { //recorre las filas de la imagen
	        	for (int y = 0; y < nro_columnas; y++){ //recorre los pixeles de cada fila	        		
	        		 try {
	        			 //System.out.println("uniendo imagen: "+n+" hilo["+x+"]["+y+"]"); 
	                     hilos[x][y].join();
	                 } catch (InterruptedException ex) {
	                     System.out.println("error" + ex);
	                   }
	        	}
	        }		            
    	}
        
        System.out.println("Guardando imagenes....");
        
        for (n = 0; n < largo_de_lista;n++) {   
        	if(a <=n && n <= b) {
        		System.out.println("Guardando imagen:  "+n);
        		LoadImage.saveImageFromMatrix(lista_de_imagenes[n],"C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[n-1]+"_conboluted.jpg");
        	}
        }
        n = largo_de_lista;
        if(a <=n && n <= b) {
    		System.out.println("Guardando imagen:  "+n);
    		LoadImage.saveImageFromMatrix(lista_de_imagenes[n],"C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[n-1]+"_conboluted.jpg");    		
    	}
        System.out.println("ESTE CLIENTE TERMINO SU PARTE");
    }

    public class tarea0101 extends Thread {

        public int max, min, id;

        tarea0101(int min_, int max_, int id_) {
            max = max_;
            min = min_;
            id = id_;

        }

        public void run() {        
        File f = new File("C:/Users/espin/OneDrive/Escritorio/imagenesparcial");
        int[][] myImg;
        int[][] myImgGS;
        // Populates the array with names of files and directories
        pathnames = f.list();
        for(int i = min; i<max ; i++){
            myImg = LoadImage.getMatrixOfImage("C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]);
            myImgGS = LoadImage.toGrayScale(myImg, false);
            lista_de_imagenes[i] = myImgGS;
            LoadImage.saveImageFromMatrix(myImgGS,"C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]+"_converted.jpg");
            }
            

        }
    }
    
    //HILOS DE CONVOLUCIÓN
    public class convolucion extends Thread {
    	public int[][] imagen;   
    	public BufferedImage input;
    	public int n,x,y,nro_filas,nro_columnas;
    	public double[][] kernel = { //kernel blur
    			/*{0.0625,0.125,0.0625},
    			{0.125,0.25,0.125},
    			{0.0625,0.125,0.0625}*/
    			/*{0.2,0,0,0,0},
    			{0,0.2,0,0,0},
    			{0,0,0.2,0,0},
    			{0,0,0,0.2,0},
    			{0,0,0,0,0.2}*/
    			{-1,-1,-1},
    			{-1,8,-1},
    			{-1,-1,-1}
    			
    	};
    	convolucion(int n_,int x_, int y_, int nro_filas_, int nro_columnas_,int[][] imagen_,BufferedImage input_) {
            this.n = n_;
        	this.x = x_;
            this.y = y_;
            this.nro_filas = nro_filas_;
            this.nro_columnas = nro_columnas_;
            this.imagen = imagen_;
            this.input = input_;
        }
        public void run() {        
        	int sum = 0;    
        	float red=0f,green=0f,bleu=0f;
			 
			//BufferedImage bufferedImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
			
        	for (int a = 0; a < kernel.length ;a++) {//parte la matriz en matrices chiquitas de 3x3 
    			for(int b = 0; b< kernel.length ; b++) {
    				int submatrizX = (x - kernel.length/2 + a + nro_filas) % nro_filas; //el % soluciona los bordes agregando los pixeles del lado opuesto
    				int submatrizY = (y - kernel.length/2 + b + nro_columnas) % nro_columnas; 
    				//submatriz[a][b] = imagen[submatrizX][submatrizY]; 
    				int RGB = input.getRGB(submatrizX,submatrizY);    

    				int R = (RGB >> 16) & 0xff; // Red Value
					int G = (RGB >> 8) & 0xff;	// Green Value
					int B = (RGB) & 0xff;		// Blue Value
					//System.out.println("R: "+ R+" G: "+G+" B: "+B);	
					// The RGB is multiplied with current kernel element and added on to the variables red, blue and green
					//System.out.println("pixel= "+imagen[submatrizX][submatrizY]);
					red += (R*kernel[a][b]);
					green += (G*kernel[a][b]);
					bleu += (B*kernel[a][b]);	      
    		        
    		        /*submatriz[a][b] = (bleu + green + red) / 3; */
						
    			}
    		}       	
        	
        	//System.out.println("RED: "+ red+" GREEN: "+green+" BLUE: "+bleu);
        	int outR, outG, outB;
			
			outR = Math.min(Math.max((int)red,0),255);
			outG = Math.min(Math.max((int)green,0),255);
			outB = Math.min(Math.max((int)bleu,0),255);
			
			Color pixelcolor = new Color(outR, outG, outB);
			sum = pixelcolor.getRGB();
			//System.out.println("SUM: "+sum+ " RED: "+ red+" GREEN: "+green+" BLUE: "+bleu);
        	
        	lista_de_imagenes[n][x][y] = sum;
        }
    }

}
