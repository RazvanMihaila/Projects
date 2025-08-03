import java.awt.*;
import java.awt.event.*;

//Clasa principala(extinde fereastra principala)
public class Triunghi extends Frame {
	Toolkit tool;//Toolkit pentru dimensiunea ecranului
    int ww, hh;//Latimea si inaltimea ecranului
	
    public Panou panou;//panoul cu butoane
    public SuprafataDeDesenare suprafata_de_desenare;//zona de desenare
	
	//Metoda roincipala care ruleaza aplicatia
    public static void main(String args[]) {
        new Triunghi();
    }

	//Constructorul clasei Triunghi
    public Triunghi() {
        // Obține dimensiunea ecranului folosind Toolkit
        tool = getToolkit();
        Dimension res = tool.getScreenSize();//Dmensiunea completa a ecranului 
        ww = res.width;//latimea
        hh = res.height;//inaltimea
		
		//Dimensiunea ecranului poate fi modificată
        setResizable(true);
		//Setăm dimensiunea ecranului
        setExtendedState(Frame.MAXIMIZED_BOTH);
		//Setăm titlul
        setTitle("Desenează un triunghi");
		//Setăm imaginea iconoței
        setIconImage(tool.getImage(getResource("images/ico.gif")));
        setLayout(null);//=>pozitionarea trebuie facuta manual
        setLocation(0, 0);//Plaseaza fereastra la coltul din stanga sus al ecranului

        //Incarca imaginea de fundal
        Image backg = tool.getImage(getResource("images/backg.jpg"));

        // Creeaza panoul
        panou = new Panou(backg);
		//Panou la pozitia 25,50 cu latimea de 150 pixeli si inaltimea hh-125(hh=inaltimea ferestrei)
        panou.setBounds(25, 50, 150, hh - 125);
        add(panou);

        // Obține referintele catre componentele din Panou
        TextArea ta = panou.getTextArea();
        Button redeseneaza = panou.getRedeseneazaButton();
        Button mediane = panou.getMedianeButton();
        Button bisectoare = panou.getBisectoareButton();
        Button inaltimi = panou.getInaltimiButton();
        Button mediatoare = panou.getMediatoareButton();
        Button cerculEuler = panou.getCerculEulerButton();
        Button laAlegere = panou.getLaAlegereButton();

        // Definește suprafața de desenare
		//this referinta pentru fereastra principala
        suprafata_de_desenare = new SuprafataDeDesenare(this, ta, redeseneaza, mediane, bisectoare, inaltimi, mediatoare, cerculEuler, laAlegere);
        suprafata_de_desenare.setBounds(200, 50, ww - 200 - 25, hh-125);//Seteaza pozitia si dimensiunea
        add(suprafata_de_desenare);//adauga

        // Eveniment pentru închiderea ferestrei
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);//inchide aplicactia
            }
        });
		setMinimumSize(new Dimension(600, 400)); // Dimensiune minimă
        setVisible(true);
    }
	
	//Metoda pentru obtinerea resurselor(imagini)
    public java.net.URL getResource(String s) {
        return this.getClass().getResource(s);//Returneaza URL-ul sursei
    }
}

//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//

//Clasa Panou contine butoanele si TextArea pentru interfata grafica
class Panou extends Panels {
    public Image backg;//Imaginea de fundal
    Toolkit tool = Toolkit.getDefaultToolkit();
    Font f = new Font("TimesRoman", Font.BOLD, 14);//Font pentru text

    Button redeseneaza, mediane, bisectoare, inaltimi, mediatoare, cerculEuler, laAlegere;
    TextArea ta;
	
	//Constructorul clase Panou
    public Panou(Image backg) {
        super(backg); // Apelează constructorul clasei părinte Panels
        setLayout(null);//elimina layout-ul implicit

        // Casetă de text fără bare de scroll
        ta = new TextArea("", 1, 20, TextArea.SCROLLBARS_NONE);
        ta.setForeground(Color.BLACK);//culoarea textului
        ta.setFont(f);//seteaza fontul
        ta.setBounds(21, 400, 110, 250);//pozitia si dimnsiunea ta
        add(ta);
		//ta este inactiv, nu poate fi modificat 
        ta.setEditable(false);
        ta.setEnabled(false);

        //Creeaza butoane
        redeseneaza = createButton("Redesenează", 21, 25);
        mediane = createButton("Mediane", 21, 75);
        bisectoare = createButton("Bisectoare", 21, 125);
        inaltimi = createButton("Înălțimi", 21, 175);
        mediatoare = createButton("Mediatoare", 21, 225);
        cerculEuler = createButton("Cercul Euler", 21, 275);
        laAlegere = createButton("Stea", 21, 325);
		
		//Butoanele sunt inactive initial, doar Redesenează este activ
        redeseneaza.setEnabled(true); // Activat
        mediane.setEnabled(false);    // Dezactivat
        bisectoare.setEnabled(false); // Dezactivat
        inaltimi.setEnabled(false);   // Dezactivat
        mediatoare.setEnabled(false); // Dezactivat
        cerculEuler.setEnabled(false);// Dezactivat
        laAlegere.setEnabled(false);  // Dezactivat

        loadImage();//Incarca imaginea de fundal
    }
	
	//Funcție pentru crearea butoanelor, cu lațimea de 110 pixeli și înălțimea de 25
    private Button createButton(String label, int x, int y) {
        Button button = new Button(label);
        button.setBounds(x, y, 110, 25);
        add(button);
        return button;//returneaz areferinta butonului
    }
	
	//Metoda care incarca imaginea de fundal
    public void loadImage() {
        try {
            MediaTracker mediaTracker = new MediaTracker(this);//Urmareste incarcarea imaginilor
            backg = tool.getImage(getResource("images/backg.jpg"));//Incarca imaginea
            mediaTracker.addImage(backg, 0);//Adauga imaginea in tracker
            mediaTracker.waitForAll();//Asteapta incarcarea completa
        } catch (Throwable throwable) {
            System.err.println("Failed to load background image: " + throwable.getMessage());
        }
    }

	//Metoda care deseneaza imaginea de fundal
    public void paint(Graphics g) {
        super.paint(g); // Desenează backgroundul
        g.drawImage(backg, 0, 0, getWidth(), getHeight(), this); // Desenează imaginea
        paintComponents(g); // Desenează butoanele și TextArea
    }
	
	
	//Get-ere pentru butoane și TextArea
    public TextArea getTextArea() {
        return ta;//Returneaza referinta catre TextArea
    }

    public Button getRedeseneazaButton() {
        return redeseneaza;
    }

    public Button getMedianeButton() {
        return mediane;
    }

    public Button getBisectoareButton() {
        return bisectoare;
    }

    public Button getInaltimiButton() {
        return inaltimi;
    }

    public Button getMediatoareButton() {
        return mediatoare;
    }

    public Button getCerculEulerButton() {
        return cerculEuler;
    }

    public Button getLaAlegereButton() {
        return laAlegere;
    }
	
	
    public java.net.URL getResource(String s) {
        return this.getClass().getResource(s);
    }
}

///************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//

class Panels extends Panel {
    public Image im, im1;//Variabile pentru imaginea de fundal si buffer-ul imaginii

    public Panels(Image im) {
        this.im = im;//Atribuie imaginea de fundal variabilei im
    }

    public void update(Graphics g) {
        super.paint(g);//Apeleaza metoda paint a clasei parinte pentru a mentine functionalitatea existenta
        Dimension dimension = size();//Obtine dimensiunile curente ale panoului
        im1 = createImage(dimension.width, dimension.height);//Creeaza o imagine tampon cu dimensiunile panoului
        pan(im1.getGraphics());//Deseneaza continutul in aceasta imagine
        g.drawImage(im1, 0, 0, this);//Deseneaza imaginea pe panou
    }

    public void pan(Graphics g) {
        Dimension dimension = size();//Dimensiunile curente le panoului
        int w = dimension.width;
        int h = dimension.height;
        g.drawImage(im, 0, 0, w, h, this); // Deseneaza imaginea de fundal scalata pe intreaga suprafata a panoului
    }
}

///************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//

// Clasă utilitară pentru o dreaptă
class Line {
    double a, b, c;

    Line(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}

//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//
//************************************************************************************************************************************************************************//

class SuprafataDeDesenare extends Canvas {
	//Variabile care indică faptul ca la inceput punctele nu sunt setate
    private boolean pointASet = false;
    private boolean pointBSet = false;
    private boolean pointCSet = false;
	
	//Variabile care inică faptul că butoanele nu sunt apăsate
	private boolean deseneaza_Mediane=false;
	private boolean deseneaza_Bisectoare=false;
	private boolean deseneaza_Inaltimi=false;
	private boolean deseneaza_Mediatoare=false;
	private boolean deseneaza_CerculEuler=false;
	private boolean deseneaza_Stea=false;
	
	
    
	//Coordonatele punctelor triunghiului
	private int pointAX, pointAY;
    private int pointBX, pointBY;
    private int pointCX, pointCY;

    private Frame parentFrame;//Referinta la fereastra parinte
    private TextArea ta;//referint ala zona de text
    private Button[] buttons;//Lista butoanelor
	
	//Variabile pentru gestionarea miscarii punctelor
	private boolean draggingA=false;//indica daca punctul A este in miscare
	private boolean draggingB=false;
	private boolean draggingC=false;
	
	private Image buffer;//buffer pt desenare
	private Graphics bufferGraphics;//Obiect grafic pentru tampon
	
	//Variabile pentru informatiile despre triunghi din TextArea
	private String informatiiTriunghi="";//Detalii despre triunghi
	private String informatiiSuplimentareTriunghi="";//Detalii suplimentare(mediane,bisectoare etc.)
	
	//Constructor
    public SuprafataDeDesenare(Frame parentFrame, TextArea ta, Button... buttons) {
        this.parentFrame = parentFrame;
        this.ta = ta;
        this.buttons = buttons;
		
		setBackground(Color.WHITE);
		
		//Butonul "Redeseneaza"
		buttons[0].addActionListener(e->{
			reseteazaSuprafataDeDesenare();
		});
		
		//Butonul "Mediane"
		buttons[1].addActionListener(e->{
			if(pointASet&&pointBSet&&pointCSet){//Verifica daca triunghiul este desenat complet
				resetCanvasState();//Reseteaza alte stari
				deseneaza_Mediane=true;//Activeaza butonul 
				parentFrame.setTitle("Triunghi și Mediane");//Actualizeaza titlul
				updateTriangleDetails();//Actualizeaza informatiile despre triunghi
				repaint();//Reimprospateaza suprafata
			}
		});
		
		//Butonul "Bisectoare"
		buttons[2].addActionListener(e->{
			if(pointASet&&pointBSet&&pointCSet){
				resetCanvasState();
				deseneaza_Bisectoare=true;
				parentFrame.setTitle("Triunghi și Bisectoare");
				updateTriangleDetails();
				repaint();
			}
		});
		
		//Butonul "Inaltimi"
		buttons[3].addActionListener(e->{
			if(pointASet&&pointBSet&&pointCSet){
				resetCanvasState();
				deseneaza_Inaltimi=true;
				parentFrame.setTitle("Triunghi și Înălțimi");
				updateTriangleDetails();
				repaint();
			}
		});
		
		//Butonul "Mediatoare"
		buttons[4].addActionListener(e->{
			if(pointASet&&pointBSet&&pointCSet){
				resetCanvasState();
				deseneaza_Mediatoare=true;
				parentFrame.setTitle("Triunghi și Mediatoare");
				updateTriangleDetails();
				repaint();
			}
		});
		
		//Butonul "Cercul Euler"
		buttons[5].addActionListener(e -> {
			if (pointASet && pointBSet && pointCSet) {
				resetCanvasState(); // Resetează alte stări
				deseneaza_CerculEuler = true; // Activăm doar desenul pentru cercul lui Euler
				parentFrame.setTitle("Triunghi și Cercul lui Euler (cercul celor 9 puncte)");
			updateTriangleDetails();
			repaint();
			}
		});
		
		//Butonul "Stea"
		buttons[6].addActionListener(e->{
			if(pointASet&&pointBSet&&pointCSet){
				resetCanvasState();
				deseneaza_Stea=true;
				parentFrame.setTitle("Triunghi și Stea");
				updateTriangleDetails();
				repaint();
			}
		});
       

        addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e){
				if(pointASet&&isNearPoint(e.getX(),e.getY(),pointAX,pointAY)){
					draggingA=true;
				}
				else if(pointBSet&&isNearPoint(e.getX(),e.getY(),pointBX,pointBY)){
					draggingB=true;
				}
				else if(pointCSet&&isNearPoint(e.getX(),e.getY(),pointCX,pointCY)){
					draggingC=true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				draggingA=false;
				draggingB=false;
				draggingC=false;
				updateTriangleDetails();
			}
			
			
			@Override
            public void mouseClicked(MouseEvent e) {
                if (!pointASet) {
                    pointAX = e.getX();
                    pointAY = e.getY();
                    pointASet = true;
                } else if (!pointBSet) {
                    pointBX = e.getX();
                    pointBY = e.getY();
                    pointBSet = true;
                } else if (!pointCSet) {
                    pointCX = e.getX();
                    pointCY = e.getY();
                    pointCSet = true;

                    // Triunghiul este complet
                    activateButtons();
                    updateTitle();
                    
                }
				updateTriangleDetails();
				
                repaint();
            }
        });
		
		addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(draggingA){
					pointAX=e.getX();
					pointAY=e.getY();
				}
				else if(draggingB){
					pointBX=e.getX();
					pointBY=e.getY();
				}
				else if(draggingC){
					pointCX=e.getX();
					pointCY=e.getY();
				}
				updateTriangleDetails();
				
				repaint();
			}
		});
    }
	
	//Metoda update
	@Override
	public void update(Graphics g){
		paint(g);//Apleaza metoda paint pentru a desena continutul
	}

    
	//Metoda paint
    @Override
    public void paint(Graphics g) {
        if(buffer==null){//Creeaza buffer-ul daca acesta nu a fost creat
			buffer=createImage(getWidth(),getHeight());
			bufferGraphics=buffer.getGraphics();
		}
		
		//curatam buffer-ul
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.fillRect(0,0,getWidth(),getHeight());
		
		
		
		//desenam triunghiul si punctele buffer
        if (pointASet) {
            bufferGraphics.setColor(Color.RED);//Seteaza culoarea pentru punct
            bufferGraphics.fillOval(pointAX - 5, pointAY - 5, 10, 10);//Coloreaza punctul
            bufferGraphics.setColor(Color.BLACK);//Seteaza culoarea pentru eticheta
            bufferGraphics.drawString("A", pointAX + 10, pointAY);//Deseneaza eticheta
        }
		
		
        if (pointASet && pointBSet) {
			bufferGraphics.setColor(Color.BLACK);//Desenama mai intai dreapta care uneste punctele 
            bufferGraphics.drawLine(pointAX, pointAY, pointBX, pointBY);
			
            bufferGraphics.setColor(Color.RED);
			bufferGraphics.fillOval(pointAX - 5, pointAY - 5, 10, 10);
            bufferGraphics.fillOval(pointBX - 5, pointBY - 5, 10, 10);
			
            bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.drawString("A", pointAX + 10, pointAY);
			bufferGraphics.drawString("B", pointBX + 10, pointBY);

			
        }

        if (pointASet && pointBSet && pointCSet) {
			bufferGraphics.setColor(Color.BLACK);
            bufferGraphics.drawLine(pointCX, pointCY, pointAX, pointAY);
            bufferGraphics.drawLine(pointCX, pointCY, pointBX, pointBY);
			
            bufferGraphics.setColor(Color.RED);
			bufferGraphics.fillOval(pointAX - 5, pointAY - 5, 10, 10);
            bufferGraphics.fillOval(pointBX - 5, pointBY - 5, 10, 10);
            bufferGraphics.fillOval(pointCX - 5, pointCY - 5, 10, 10);
			
            bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.drawString("A", pointAX + 10, pointAY);
			bufferGraphics.drawString("B", pointBX + 10, pointBY);
            bufferGraphics.drawString("C", pointCX + 10, pointCY);

			
			
			if(deseneaza_Mediane){//Daca butonul Mediane este activ desenam medianele
				calculeazaMediane(bufferGraphics);
			}
			
			else if(deseneaza_Bisectoare){
				calculeazaBisectoare(bufferGraphics);
			}
			else if(deseneaza_Inaltimi){
				calculeazaInaltimi(bufferGraphics);
			}
			else if(deseneaza_Mediatoare){
				calculeazaMediatoare(bufferGraphics);
			}
			else if(deseneaza_CerculEuler){
				calculeazaCerculEuler(bufferGraphics);
			}
			else if(deseneaza_Stea){
				calculeazaStea(bufferGraphics);
			}
        }
		
		//desenam buffer-ul pe ecran
		g.drawImage(buffer,0,0,this);
    }
	
	//Metoda care verifica daca un punct (x,y) este aproape de alt punct (px,py)
	private boolean isNearPoint(int x,int y,int px,int py){
		int prag_maxim=10;//Distanta maxima pt selectia punctului
		//Compara distanta dintre puncte  cu valoarea pragului maxim 
		return Math.sqrt(Math.pow(x-px,2)+Math.pow(y-py,2))<=prag_maxim;
	}
	
	//Metoda folosita pentru a activa butoanele
	private void activateButtons() {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }
	
	//Metoda care modifica titlul
    private void updateTitle() {
        parentFrame.setTitle("Triunghi");
    }

	//Metoda pentru actualizarea detaliilor triunghiului
    private void updateTriangleDetails() {
        if(pointASet&&pointBSet&&pointCSet){
			// Lungimea laturilor
			double AB = Math.sqrt(Math.pow(pointBX - pointAX, 2) + Math.pow(pointBY - pointAY, 2));
			double AC = Math.sqrt(Math.pow(pointCX - pointAX, 2) + Math.pow(pointCY - pointAY, 2));
			double BC = Math.sqrt(Math.pow(pointCX - pointBX, 2) + Math.pow(pointCY - pointBY, 2));

			// Aria
			double semiPerimetru = (AB + BC + AC) / 2;
			double aria = Math.sqrt(semiPerimetru * (semiPerimetru - AB) * (semiPerimetru - BC) * (semiPerimetru - AC));

			// Afișare detalii în TextArea
			informatiiTriunghi=String.format("AB=%.2f\nBC=%.2f\nAC=%.2f\nAria=%.1f", AB, BC, AC, aria);
			
			//Actualizam TextArea pastrand informatiile aditionale
			ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
		}
	}
	
	//Metoda pentru calcularea medianelor
	private void calculeazaMediane(Graphics g){
		//Mijloacele laturilor
		int mijlocAX=(pointBX+pointCX)/2;
		int mijlocAY=(pointBY+pointCY)/2;
		int mijlocBX=(pointAX+pointCX)/2;
		int mijlocBY=(pointAY+pointCY)/2;
		int mijlocCX=(pointBX+pointAX)/2;
		int mijlocCY=(pointBY+pointAY)/2;
		
		//Centrul de greutate
		int centruGX=(pointAX+pointBX+pointCX)/3;
		int centruGY=(pointAY+pointBY+pointCY)/3;
		
		//Desenam triunghiul
		g.setColor(Color.BLACK);
        g.drawLine(pointCX, pointCY, pointAX, pointAY);
        g.drawLine(pointCX, pointCY, pointBX, pointBY);
		
		//Desenam medianele
		g.setColor(Color.BLUE);
		g.drawLine(pointAX,pointAY,mijlocAX,mijlocAY);
		g.drawLine(pointBX,pointBY,mijlocBX,mijlocBY);
		g.drawLine(pointCX,pointCY,mijlocCX,mijlocCY);
		
		//Desenam varfurile triunghiului
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
        g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
        g.fillOval(pointCX - 5, pointCY - 5, 10, 10);
		
		//Desenarea mijloacelor si a centrului de greutate
		g.setColor(Color.YELLOW);
		g.fillOval(mijlocAX-5,mijlocAY-5,10,10);
		g.fillOval(mijlocBX-5,mijlocBY-5,10,10);
		g.fillOval(mijlocCX-5,mijlocCY-5,10,10);
		g.fillOval(centruGX-5,centruGY-5,10,10);
		
		//Etichete pentru varfurile triunghiului 
		g.setColor(Color.BLACK);
		g.drawString("A", pointAX + 10, pointAY);
		g.drawString("B", pointBX + 10, pointBY);
        g.drawString("C", pointCX + 10, pointCY);
		
		//Etichete pentru mijloacele laturilor
		g.setColor(Color.BLACK);
		g.drawString("A'",mijlocAX+10,mijlocAY);
		g.drawString("B'",mijlocBX+10,mijlocBY);
		g.drawString("C'",mijlocCX+10,mijlocCY);
		
		g.drawString("G",centruGX+10,centruGY);
		
		//Caluculam lungimile medianelor
		double medianaA=Math.sqrt(Math.pow(pointAX-mijlocAX,2)+Math.pow(pointAY-mijlocAY,2));
		double medianaB=Math.sqrt(Math.pow(pointBX-mijlocBX,2)+Math.pow(pointBY-mijlocBY,2));
		double medianaC=Math.sqrt(Math.pow(pointCX-mijlocCX,2)+Math.pow(pointCY-mijlocCY,2));
		
		//Actualizam TextArea
		informatiiSuplimentareTriunghi = String.format("\nAA'=%.2f\nBB'=%.2f\nCC'=%.2f\n", medianaA, medianaB, medianaC);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
		
	}
	
	//Metoda pentru calcularea bisectoarelor
	private void calculeazaBisectoare(Graphics g){
		// Lungimile laturilor
		double AB = Math.sqrt(Math.pow(pointBX - pointAX, 2) + Math.pow(pointBY - pointAY, 2));
		double AC = Math.sqrt(Math.pow(pointCX - pointAX, 2) + Math.pow(pointCY - pointAY, 2));
		double BC = Math.sqrt(Math.pow(pointCX - pointBX, 2) + Math.pow(pointCY - pointBY, 2));
		
		int centruIX,centruIY;//coordonatele punctului I(intersectia bisectoarelor)
		double raza;//raza cercului incris
		
		// Centrul cercului înscris
		double perimetru = AB + AC + BC;
		centruIX = (int) ((BC * pointAX + AC * pointBX + AB * pointCX) / perimetru);
		centruIY = (int) ((BC * pointAY + AC * pointBY + AB * pointCY) / perimetru);

		// Raza cercului înscris
		double semiPerimetru = perimetru / 2;
		double aria = Math.sqrt(semiPerimetru * (semiPerimetru - AB) * (semiPerimetru - BC) * (semiPerimetru - AC));
		raza = (2 * aria) / perimetru;

		// Desenăm cercul înscris
		g.setColor(getBackground().darker());
		g.fillOval((int) (centruIX - raza), (int) (centruIY - raza), (int) (2 * raza), (int) (2 * raza));
		g.setColor(Color.BLUE);
		g.drawOval((int) (centruIX - raza), (int) (centruIY - raza), (int) (2 * raza), (int) (2 * raza));

		//Desenam triunghiul
		g.setColor(Color.BLACK);
        g.drawLine(pointCX, pointCY, pointAX, pointAY);
        g.drawLine(pointCX, pointCY, pointBX, pointBY);

		// Desenăm bisectoarele
		g.setColor(Color.BLUE);
		g.drawLine(pointAX, pointAY, centruIX, centruIY);
		g.drawLine(pointBX, pointBY, centruIX, centruIY);
		g.drawLine(pointCX, pointCY, centruIX, centruIY);
		
		//Desenam varfurile triunghiului
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
        g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
        g.fillOval(pointCX - 5, pointCY - 5, 10, 10);
		
		// Desenăm punctul I
		g.setColor(Color.YELLOW);
		g.fillOval(centruIX - 5, centruIY - 5, 10, 10);
		
		//Etichetele
		g.setColor(Color.BLACK);
		g.drawString("I", centruIX + 10, centruIY);
		
		//Etichete pentru varfurile triunghiului 
		g.setColor(Color.BLACK);
		g.drawString("A", pointAX + 10, pointAY);
		g.drawString("B", pointBX + 10, pointBY);
        g.drawString("C", pointCX + 10, pointCY);

		// Actualizăm `TextArea`
		double AI = Math.sqrt(Math.pow(centruIX - pointAX, 2) + Math.pow(centruIY - pointAY, 2));
		double BI = Math.sqrt(Math.pow(centruIX - pointBX, 2) + Math.pow(centruIY - pointBY, 2));
		double CI = Math.sqrt(Math.pow(centruIX - pointCX, 2) + Math.pow(centruIY - pointCY, 2));
		informatiiSuplimentareTriunghi=String.format("\nRaza=%.2f\nAI=%.2f\nBI=%.2f\nCI=%.2f",raza, AI, BI, CI);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
	}
	
	//Metoda pentru calcularea inaltimilor
	private void calculeazaInaltimi(Graphics g) {
		// Calculăm ecuațiile dreptei pentru laturile triunghiului
		Line lBC = calculEcuatieDreapta(pointBX, pointBY, pointCX, pointCY); // Latura BC
		Line lAC = calculEcuatieDreapta(pointAX, pointAY, pointCX, pointCY); // Latura AC
		Line lAB = calculEcuatieDreapta(pointAX, pointAY, pointBX, pointBY); // Latura AB

		// Calculăm ecuațiile înălțimilor
		Line hA = perpendiculara(lBC, pointAX, pointAY); // Înălțimea din A
		Line hB = perpendiculara(lAC, pointBX, pointBY); // Înălțimea din B
		Line hC = perpendiculara(lAB, pointCX, pointCY); // Înălțimea din C

		// Intersecțiile înălțimilor cu laturile triunghiului (A', B', C')
		Point intersectionA = intersectieDrepte(lBC, hA); // Intersecția dintre înălțimea din A și latura BC
		Point intersectionB = intersectieDrepte(lAC, hB); // Intersecția dintre înălțimea din B și latura AC
		Point intersectionC = intersectieDrepte(lAB, hC); // Intersecția dintre înălțimea din C și latura AB

		// Calculăm ortocentrul H (intersecția înălțimilor)
		Point ortocentru = intersectieDrepte(hA, hB);

		// Desenăm triunghiul
		g.setColor(Color.BLACK);
		g.drawLine(pointAX, pointAY, pointBX, pointBY); // Latura AB
		g.drawLine(pointBX, pointBY, pointCX, pointCY); // Latura BC
		g.drawLine(pointCX, pointCY, pointAX, pointAY); // Latura AC

		// Desenăm înălțimile până la ortocentru sau punctele de intersecție
		g.setColor(Color.BLUE);
		if (intersectionA != null && ortocentru != null) {
			g.drawLine(pointAX, pointAY, intersectionA.x, intersectionA.y); // De la A la A'
			g.drawLine(intersectionA.x, intersectionA.y, ortocentru.x, ortocentru.y); // A' la H
		}
		if (intersectionB != null && ortocentru != null) {
			g.drawLine(pointBX, pointBY, intersectionB.x, intersectionB.y); // De la B la B'
			g.drawLine(intersectionB.x, intersectionB.y, ortocentru.x, ortocentru.y); // B' la H
		}
		if (intersectionC != null && ortocentru != null) {
			g.drawLine(pointCX, pointCY, intersectionC.x, intersectionC.y); // De la C la C'
			g.drawLine(intersectionC.x, intersectionC.y, ortocentru.x, ortocentru.y); // C' la H
		}
		
		//Desenam varfurile triunghiului
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
        g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
        g.fillOval(pointCX - 5, pointCY - 5, 10, 10);

		// Desenăm punctele galbene A', B', C'
		g.setColor(Color.YELLOW);
		if (intersectionA != null) {
			g.fillOval(intersectionA.x - 5, intersectionA.y - 5, 10, 10);
			g.setColor(Color.BLACK);
			g.drawString("A'", intersectionA.x + 10, intersectionA.y);
		}
		if (intersectionB != null) {
			g.setColor(Color.YELLOW);
			g.fillOval(intersectionB.x - 5, intersectionB.y - 5, 10, 10);
			g.setColor(Color.BLACK);
			g.drawString("B'", intersectionB.x + 10, intersectionB.y);
		}
		if (intersectionC != null) {
			g.setColor(Color.YELLOW);
			g.fillOval(intersectionC.x - 5, intersectionC.y - 5, 10, 10);
			g.setColor(Color.BLACK);
			g.drawString("C'", intersectionC.x + 10, intersectionC.y);
		}

		// Desenăm ortocentrul H
		if (ortocentru != null) {
			g.setColor(Color.YELLOW);
			g.fillOval(ortocentru.x - 5, ortocentru.y - 5, 10, 10);
			g.setColor(Color.BLACK);
			g.drawString("H", ortocentru.x + 10, ortocentru.y);
		}
		
		
		//Etichetele
		g.setColor(Color.BLACK);
	
		g.drawString("A", pointAX + 10, pointAY);
		g.drawString("B", pointBX + 10, pointBY);
        g.drawString("C", pointCX + 10, pointCY);;

		// Calculăm lungimile laturilor și aria triunghiului
		double AB = Math.sqrt(Math.pow(pointBX - pointAX, 2) + Math.pow(pointBY - pointAY, 2));
		double AC = Math.sqrt(Math.pow(pointCX - pointAX, 2) + Math.pow(pointCY - pointAY, 2));
		double BC = Math.sqrt(Math.pow(pointCX - pointBX, 2) + Math.pow(pointCY - pointBY, 2));
		double semiPerimetru = (AB + AC + BC) / 2;
		double aria = Math.sqrt(semiPerimetru * (semiPerimetru - AB) * (semiPerimetru - AC) * (semiPerimetru - BC));

		// Calculăm lungimile înălțimilor
		double heightA = 2 * aria / BC;
		double heightB = 2 * aria / AC;
		double heightC = 2 * aria / AB;

		// Afișăm informațiile în TextArea
		informatiiSuplimentareTriunghi=String.format("\nhA=%.2f\nhB=%.2f\nhC=%.2f", heightA, heightB, heightC);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
	}

	//Metoda pentru calcularea Mediatoarelor
	private void calculeazaMediatoare(Graphics g) {
		// Calculăm mijloacele laturilor
		Point mijlocAB = new Point((pointAX + pointBX) / 2, (pointAY + pointBY) / 2); // Mijloc AB
		Point mijlocBC = new Point((pointBX + pointCX) / 2, (pointBY + pointCY) / 2); // Mijloc BC
		Point mijlocAC = new Point((pointAX + pointCX) / 2, (pointAY + pointCY) / 2); // Mijloc AC
		
		// Calculăm ecuațiile mediatoarelor
		Line mediatoareAB = perpendiculara(calculEcuatieDreapta(pointAX, pointAY, pointBX, pointBY), mijlocAB.x, mijlocAB.y);
		Line mediatoareBC = perpendiculara(calculEcuatieDreapta(pointBX, pointBY, pointCX, pointCY), mijlocBC.x, mijlocBC.y);
		Line mediatoareAC = perpendiculara(calculEcuatieDreapta(pointAX, pointAY, pointCX, pointCY), mijlocAC.x, mijlocAC.y);

		// Calculăm centrul cercului circumscris (intersecția mediatoarelor)
		Point centruO = intersectieDrepte(mediatoareAB, mediatoareBC);

		// Calculăm raza cercului circumscris
		double raza= Math.sqrt(Math.pow(centruO.x - pointAX, 2) + Math.pow(centruO.y - pointAY, 2));
		
		// Desenăm cercul circumscris
		g.setColor(Color.BLUE);
		g.drawOval((int) (centruO.x - raza), (int) (centruO.y - raza), (int) (2 * raza), (int) (2 * raza));
		
		// Desenăm triunghiul
		g.setColor(Color.BLACK);
		g.drawLine(pointAX, pointAY, pointBX, pointBY); // Latura AB
		g.drawLine(pointBX, pointBY, pointCX, pointCY); // Latura BC
		g.drawLine(pointCX, pointCY, pointAX, pointAY); // Latura AC

		// Desenăm segmentele OA', OB', OC'
		g.setColor(Color.BLUE);
		g.drawLine(centruO.x, centruO.y, mijlocAB.x, mijlocAB.y); // OA'
		g.drawLine(centruO.x, centruO.y, mijlocBC.x, mijlocBC.y); // OB'
		g.drawLine(centruO.x, centruO.y, mijlocAC.x, mijlocAC.y); // OC'

		// Desenăm mijloacele laturilor
		g.setColor(Color.YELLOW);
		g.fillOval(mijlocAB.x - 5, mijlocAB.y - 5, 10, 10);
		g.fillOval(mijlocBC.x - 5, mijlocBC.y - 5, 10, 10);
		g.fillOval(mijlocAC.x - 5, mijlocAC.y - 5, 10, 10);
		
		//Desenam varfurile triunghiului
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
		g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
		g.fillOval(pointCX - 5, pointCY - 5, 10, 10);

		// Desenăm centrul cercului circumscris
		g.setColor(Color.YELLOW);
		g.fillOval(centruO.x - 5, centruO.y - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("O", centruO.x + 10, centruO.y);

		// Etichetăm mijloacele laturilor
		g.setColor(Color.BLACK);
		g.drawString("C'", mijlocAB.x + 10, mijlocAB.y);
		g.drawString("A'", mijlocBC.x + 10, mijlocBC.y);
		g.drawString("B'", mijlocAC.x + 10, mijlocAC.y);

	   
		// Calculăm lungimile laturilor și aria triunghiului
		double AB = Math.sqrt(Math.pow(pointBX - pointAX, 2) + Math.pow(pointBY - pointAY, 2));
		double AC = Math.sqrt(Math.pow(pointCX - pointAX, 2) + Math.pow(pointCY - pointAY, 2));
		double BC = Math.sqrt(Math.pow(pointCX - pointBX, 2) + Math.pow(pointCY - pointBY, 2));
		double semiPerimetru = (AB + AC + BC) / 2;
		double aria = Math.sqrt(semiPerimetru * (semiPerimetru - AB) * (semiPerimetru - AC) * (semiPerimetru - BC));

		// Afișăm informațiile în TextArea
		informatiiSuplimentareTriunghi=String.format("\nRaza=%.2f", raza);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
	}

	//Metoda pentru calcularea Cercului lui Euler
	private void calculeazaCerculEuler(Graphics g) {
		// Calculăm mijloacele laturilor
		Point mijlocAB = new Point((pointAX + pointBX) / 2, (pointAY + pointBY) / 2); // Mijloc AB
		Point mijlocBC = new Point((pointBX + pointCX) / 2, (pointBY + pointCY) / 2); // Mijloc BC
		Point mijlocAC = new Point((pointAX + pointCX) / 2, (pointAY + pointCY) / 2); // Mijloc AC

		// Calculăm ortocentrul H
		Line lBC = calculEcuatieDreapta(pointBX, pointBY, pointCX, pointCY); // Latura BC
		Line lAC = calculEcuatieDreapta(pointAX, pointAY, pointCX, pointCY); // Latura AC
		Line lAB = calculEcuatieDreapta(pointAX, pointAY, pointBX, pointBY); // Latura AB

		Line hA = perpendiculara(lBC, pointAX, pointAY); // Înălțimea din A
		Line hB = perpendiculara(lAC, pointBX, pointBY); // Înălțimea din B
		Line hC = perpendiculara(lAB, pointCX, pointCY); // Înălțimea din C

		Point intersectionA = intersectieDrepte(lBC, hA); // Picior A'
		Point intersectionB = intersectieDrepte(lAC, hB); // Picior B'
		Point intersectionC = intersectieDrepte(lAB, hC); // Picior C'

		Point ortocentru = intersectieDrepte(hA, hB); // Ortocentrul H

		// Calculăm centrul cercului circumscris O
		Line mediatoareAB = perpendiculara(lAB, mijlocAB.x, mijlocAB.y);
		Line mediatoareBC = perpendiculara(lBC, mijlocBC.x, mijlocBC.y);
		Point centruO = intersectieDrepte(mediatoareAB, mediatoareBC);

		// Calculăm mijloacele segmentelor AH, BH, CH
		Point mijlocAH = new Point((pointAX + ortocentru.x) / 2, (pointAY + ortocentru.y) / 2);
		Point mijlocBH = new Point((pointBX + ortocentru.x) / 2, (pointBY + ortocentru.y) / 2);
		Point mijlocCH = new Point((pointCX + ortocentru.x) / 2, (pointCY + ortocentru.y) / 2);

		// Calculăm mijlocul segmentului OH
		Point mijlocOH = new Point((centruO.x + ortocentru.x) / 2, (centruO.y + ortocentru.y) / 2);

		// Calculăm raza cercului lui Euler
		double razaEuler = Math.sqrt(Math.pow(mijlocOH.x - mijlocAB.x, 2) + Math.pow(mijlocOH.y - mijlocAB.y, 2));

		
		// Desenăm discul cercului lui Euler (mai întunecat decât fundalul)
		g.setColor(getBackground().darker()); // Nuanță mai întunecată a fundalului
		g.fillOval((int) (mijlocOH.x - razaEuler), (int) (mijlocOH.y - razaEuler), (int) (2 * razaEuler), (int) (2 * razaEuler));

		// Desenăm cercul lui Euler (cu verde)
		g.setColor(Color.GREEN);
		g.drawOval((int) (mijlocOH.x - razaEuler), (int) (mijlocOH.y - razaEuler), (int) (2 * razaEuler), (int) (2 * razaEuler));
		
		// Desenăm triunghiul
		g.setColor(Color.BLACK);
		g.drawLine(pointAX, pointAY, pointBX, pointBY); // Latura AB
		g.drawLine(pointBX, pointBY, pointCX, pointCY); // Latura BC
		g.drawLine(pointCX, pointCY, pointAX, pointAY); // Latura AC
		
		// Desenăm segmentul OH (cu roșu)
		g.setColor(Color.RED);
		g.drawLine(centruO.x, centruO.y, ortocentru.x, ortocentru.y);
		
		// Desenăm liniile albastre care unesc centrul cercului circumscris O cu mijloacele laturilor
		g.setColor(Color.BLUE);
		g.drawLine(centruO.x, centruO.y, mijlocAB.x, mijlocAB.y); // Linia O -> A'
		g.drawLine(centruO.x, centruO.y, mijlocBC.x, mijlocBC.y); // Linia O -> B'
		g.drawLine(centruO.x, centruO.y, mijlocAC.x, mijlocAC.y); // Linia O -> C'

		// Desenăm înălțimile până la ortocentru sau punctele de intersecție
		g.setColor(Color.BLUE);
		if (intersectionA != null && ortocentru != null) {
			g.drawLine(pointAX, pointAY, intersectionA.x, intersectionA.y); // De la A la A'
			g.drawLine(intersectionA.x, intersectionA.y, ortocentru.x, ortocentru.y); // A' la H
		}
		if (intersectionB != null && ortocentru != null) {
			g.drawLine(pointBX, pointBY, intersectionB.x, intersectionB.y); // De la B la B'
			g.drawLine(intersectionB.x, intersectionB.y, ortocentru.x, ortocentru.y); // B' la H
		}
		if (intersectionC != null && ortocentru != null) {
			g.drawLine(pointCX, pointCY, intersectionC.x, intersectionC.y); // De la C la C'
			g.drawLine(intersectionC.x, intersectionC.y, ortocentru.x, ortocentru.y); // C' la H
		}
		
		//Desenam punctele		
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("A", pointAX + 10, pointAY);
		
		g.setColor(Color.RED);
		g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("B", pointBX + 10, pointBY);
		
		g.setColor(Color.RED);
		g.fillOval(pointCX - 5, pointCY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("C", pointCX + 10, pointCY);
				

		// Desenăm mijloacele laturilor (galben, fără etichete)
		g.setColor(Color.YELLOW);
		g.fillOval(mijlocAB.x - 5, mijlocAB.y - 5, 10, 10);
		g.fillOval(mijlocBC.x - 5, mijlocBC.y - 5, 10, 10);
		g.fillOval(mijlocAC.x - 5, mijlocAC.y - 5, 10, 10);

		// Desenăm picioarele înălțimilor (galben, fără etichete)
		if (intersectionA != null) g.fillOval(intersectionA.x - 5, intersectionA.y - 5, 10, 10);
		if (intersectionB != null) g.fillOval(intersectionB.x - 5, intersectionB.y - 5, 10, 10);
		if (intersectionC != null) g.fillOval(intersectionC.x - 5, intersectionC.y - 5, 10, 10);

		// Desenăm mijloacele segmentelor AH, BH, CH (galben, fără etichete)
		g.fillOval(mijlocAH.x - 5, mijlocAH.y - 5, 10, 10);
		g.fillOval(mijlocBH.x - 5, mijlocBH.y - 5, 10, 10);
		g.fillOval(mijlocCH.x - 5, mijlocCH.y - 5, 10, 10);

		// Desenăm ortocentrul H (galben întunecat, etichetă H)
		g.setColor(Color.ORANGE);
		g.fillOval(ortocentru.x - 5, ortocentru.y - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("H", ortocentru.x + 10, ortocentru.y);

		// Desenăm centrul cercului circumscris O (galben întunecat, etichetă O)
		g.setColor(Color.ORANGE);
		g.fillOval(centruO.x - 5, centruO.y - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("O", centruO.x + 10, centruO.y);

		// Desenăm mijlocul segmentului OH (galben întunecat, etichetă E)
		g.setColor(Color.ORANGE);
		g.fillOval(mijlocOH.x - 5, mijlocOH.y - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("E", mijlocOH.x + 10, mijlocOH.y);

		// Calculăm lungimile laturilor și aria triunghiului
		double AB = Math.sqrt(Math.pow(pointBX - pointAX, 2) + Math.pow(pointBY - pointAY, 2));
		double AC = Math.sqrt(Math.pow(pointCX - pointAX, 2) + Math.pow(pointCY - pointAY, 2));
		double BC = Math.sqrt(Math.pow(pointCX - pointBX, 2) + Math.pow(pointCY - pointBY, 2));
		double semiPerimetru = (AB + AC + BC) / 2;
		double aria = Math.sqrt(semiPerimetru * (semiPerimetru - AB) * (semiPerimetru - AC) * (semiPerimetru - BC));
		double OH = Math.sqrt(Math.pow(centruO.x - ortocentru.x, 2) + Math.pow(centruO.y - ortocentru.y, 2));

		// Afișăm informațiile în TextArea
		informatiiSuplimentareTriunghi=String.format("\nRaza=%.2f\nOH=%.2f",razaEuler,OH);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
	}

	//Metoda pentru calcularea "LaAlegere"
	private void calculeazaStea(Graphics g) {
		// Centrul geometric al triunghiului (centroidul)
		int centruX = (pointAX + pointBX + pointCX) / 3;
		int centruY = (pointAY + pointBY + pointCY) / 3;

		// Dimensiunea triunghiului
		int maxDim = Math.max(Math.abs(pointAX - pointBX), Math.max(Math.abs(pointBX - pointCX), Math.abs(pointCX - pointAX)));

		// Rază mai mică pentru stea
		int radiusOuter = maxDim / 8; // Dimensiunea exterioară redusă a stelei
		int radiusInner = radiusOuter / 2; // Dimensiunea interioară a stelei

		// Calculăm punctele stelei (5 colțuri)
		double angleStep = Math.PI / 5; // 36 de grade între fiecare punct
		int[] xPoints = new int[10];
		int[] yPoints = new int[10];

		for (int i = 0; i < 10; i++) {
			double angle = i * angleStep - Math.PI / 2; // Pornim de la vârful de sus al stelei
			if (i % 2 == 0) { // Colțurile exterioare
				xPoints[i] = centruX + (int) (radiusOuter * Math.cos(angle));
				yPoints[i] = centruY + (int) (radiusOuter * Math.sin(angle));
			} else { // Colțurile interioare
				xPoints[i] = centruX + (int) (radiusInner * Math.cos(angle));
				yPoints[i] = centruY + (int) (radiusInner * Math.sin(angle));
			}
		}
		
		double starArea=calculeazaAriaStelei(xPoints,yPoints,10);

		// Distribuim cerculețele galbene în interiorul triunghiului
		int circleRadius = 10; // Dimensiunea cerculețelor
		int spacing = Math.max(20, maxDim / 20); // Distanța dintre cerculețe, proporțională cu dimensiunea triunghiului

		for (int x = Math.min(pointAX, Math.min(pointBX, pointCX)); x <= Math.max(pointAX, Math.max(pointBX, pointCX)); x += spacing) {
			for (int y = Math.min(pointAY, Math.min(pointBY, pointCY)); y <= Math.max(pointAY, Math.max(pointBY, pointCY)); y += spacing) {
				if (isInsideTriangle(x, y, pointAX, pointAY, pointBX, pointBY, pointCX, pointCY)) {
					g.setColor(Color.YELLOW);
					g.fillOval(x - circleRadius, y - circleRadius, 2 * circleRadius, 2 * circleRadius);
				}
			}
		}
		
		// Colorăm interiorul stelei cu galben
		g.setColor(Color.YELLOW);
		g.fillPolygon(xPoints, yPoints, 10);
		
		// Trasăm marginile stelei cu negru
		g.setColor(Color.BLACK);
		g.drawPolygon(xPoints, yPoints, 10);
		
		 // Desenăm triunghiul exterior (pentru referință, negru)
		g.setColor(Color.BLACK);
		g.drawLine(pointAX, pointAY, pointBX, pointBY);
		g.drawLine(pointBX, pointBY, pointCX, pointCY);
		g.drawLine(pointCX, pointCY, pointAX, pointAY);
		
		//Desenam punctele		
		g.setColor(Color.RED);
		g.fillOval(pointAX - 5, pointAY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("A", pointAX + 10, pointAY);
			
		g.setColor(Color.RED);
		g.fillOval(pointBX - 5, pointBY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("B", pointBX + 10, pointBY);
			
		g.setColor(Color.RED);
		g.fillOval(pointCX - 5, pointCY - 5, 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("C", pointCX + 10, pointCY);
		

		// Marcăm punctele stelei cu albastre
		g.setColor(Color.BLUE);
		for (int i = 0; i < 10; i++) {
			g.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
		}

		// Afișăm informațiile în TextArea
		informatiiSuplimentareTriunghi=String.format("\nA_stea=%.1f",starArea);
		ta.setText(informatiiTriunghi+informatiiSuplimentareTriunghi);
	}

	// Metodă pentru a verifica dacă un punct este în interiorul triunghiului
	private boolean isInsideTriangle(int px, int py, int ax, int ay, int bx, int by, int cx, int cy) {
		//Calculeaza aria triunghiului original(ABC( folosind determinanti
		double ariaOrig = Math.abs((ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) / 2.0);
		//Calculeaza aria triunghiului format din punctul P si laturile AB si BC
		double aria1 = Math.abs((px * (by - cy) + bx * (cy - py) + cx * (py - by)) / 2.0);
		//Calculeaza aria triunghiului format din punctul P si laturile AC si BC
		double aria2 = Math.abs((ax * (py - cy) + px * (cy - ay) + cx * (ay - py)) / 2.0);
		//Calculeaza aria triunghiului format din punctul P si laturile AB si AC
		double aria3 = Math.abs((ax * (by - py) + bx * (py - ay) + px * (ay - by)) / 2.0);

		//Verifica daca suma ariilor 1,2,3 = cu ariaOrig
		return Math.abs(ariaOrig - (aria1 + aria2 + aria3)) < 1e-6;
	}


	// Metoda pentru calularea ecuației unei drepte prin două puncte
	private Line calculEcuatieDreapta(int x1, int y1, int x2, int y2) {
		double a = y2 - y1;
		double b = x1 - x2;
		double c = x2 * y1 - x1 * y2;
		return new Line(a, b, c);
	}

	//Metoda pentru calcularea punctului de intersecție dintre două drepte
	private Point intersectieDrepte(Line l1, Line l2) {
		double determinant = l1.a * l2.b - l2.a * l1.b;
		if (determinant == 0) {
			return null; // Dreptele sunt paralele
		}
		double x = (l2.b * -l1.c - l1.b * -l2.c) / determinant;
		double y = (l1.a * -l2.c - l2.a * -l1.c) / determinant;
		return new Point((int) x, (int) y);
	}

	//Metoda pentru calcularea dreaptei perpendiculare pe o altă dreaptă care trece printr-un punct
	private Line perpendiculara(Line l, int px, int py) {
		double a = -l.b;
		double b = l.a;
		double c = -(a * px + b * py);
		return new Line(a, b, c);
	}
	
	//Metoda pentru resetarea modificarilor facute pe triunghi
	private void resetCanvasState() {
		deseneaza_Mediane = false;
		deseneaza_Bisectoare = false;
		deseneaza_Inaltimi=false;
		deseneaza_Mediatoare=false;
		deseneaza_CerculEuler=false;
		deseneaza_Stea=false;
		
		ta.setText(""); // Curățăm zona de text
		repaint(); // Reîmprospătăm canvas-ul
	}
	
	//Metoda pentru resetarea suprafetei de desen
	private void reseteazaSuprafataDeDesenare() {
		// Resetăm punctele triunghiului
		pointASet = false;
		pointBSet = false;
		pointCSet = false;

		// Resetăm variabilele suplimentare
		deseneaza_Mediane = false;
		deseneaza_Bisectoare = false;
		deseneaza_Inaltimi=false;
		deseneaza_Mediatoare=false;
		deseneaza_CerculEuler=false;
		deseneaza_Stea=false;

		// Resetăm conținutul TextArea
		ta.setText("");

		// Resetăm bara de titlu
		parentFrame.setTitle("Desenează un triunghi");

		// Dezactivăm toate butoanele, cu excepția Redesenează
		for (int i = 1; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
		}

		// Ștergem suprafața de desenare și permitem reluarea procesului de desenare
		repaint();
	}
	
	//Metoda pentru calcuarea ariei stelei
	private double calculeazaAriaStelei(int[] xPoints, int[] yPoints, int numPoints) {
    double area = 0;
		for (int i = 0; i < numPoints - 1; i++) {
			area += xPoints[i] * yPoints[i + 1] - yPoints[i] * xPoints[i + 1];
		}
		// Adăugăm ultima componentă (de la ultimul punct la primul punct)
		area += xPoints[numPoints - 1] * yPoints[0] - yPoints[numPoints - 1] * xPoints[0];
		return Math.abs(area) / 2.0;
	}

}


