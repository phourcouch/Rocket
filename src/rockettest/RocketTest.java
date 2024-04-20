package rockettest;


import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.*;

 

public class RocketTest extends JFrame implements Runnable {

    boolean animateFirstTime = true;
    Image image;
    Graphics2D g;

    Image outerSpaceImage;
    
//variables for rocket. 
    Rocket rocket;

    double frameRate = 25.0;
    
    boolean gameOver;
    int timeCount;
    
//add or modify.  Variable already added for you.
    int score;
    
    static RocketTest frame;
    public static void main(String[] args) {
        frame = new RocketTest();
        frame.setSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public RocketTest() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                
                if (gameOver)
                    return;
                
                if (e.VK_UP == e.getKeyCode()) {
                    rocket.IncreaseYSpeed(1);
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocket.IncreaseYSpeed(-1);
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocket.IncreaseXSpeed(-1);
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocket.IncreaseXSpeed(1);
                } else if (e.VK_SPACE == e.getKeyCode()) {
                    Missile.Create(rocket.getFaceRight(),rocket.getXPos(),rocket.getYPos());
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }

 

////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || Window.xsize != getSize().width || Window.ysize != getSize().height) {
            Window.xsize = getSize().width;
            Window.ysize = getSize().height;
            image = createImage(Window.xsize, Window.ysize);
            g = (Graphics2D) image.getGraphics();
            Drawing.setDrawingInfo(g,this);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, Window.xsize, Window.ysize);

        int x[] = {Window.getX(0), Window.getX(Window.getWidth2()), Window.getX(Window.getWidth2()), Window.getX(0), Window.getX(0)};
        int y[] = {Window.getY(0), Window.getY(0), Window.getY(Window.getHeight2()), Window.getY(Window.getHeight2()), Window.getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,Window.getX(0),Window.getY(0),
                Window.getWidth2(),Window.getHeight2(),this);

        Star.Draw();
//add or modify.  Draw the planets.   
        Planet.Draw();
        Missile.Draw();
        rocket.Draw();
   
        if (gameOver) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial",Font.PLAIN,50));
            g.drawString("GAME OVER",60,250);              
        }
//add or modify.  Display the score.        
        g.setColor(Color.black);
        g.setFont(new Font("Andy",Font.PLAIN,27));
        g.drawString("score = " + score,20,52);  
            
        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 1/frameRate;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        timeCount = 0;
        gameOver = false;
        rocket = new Rocket();
        Star.Reset();
//add or modify.  Reset the planets.
        Planet.Reset();
        Missile.Reset();
//add or modify.  Init the score.
        score = 0;

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (Window.xsize != getSize().width || Window.ysize != getSize().height) {
                Window.xsize = getSize().width;
                Window.ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            Rocket.image = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");    
            Star.image = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");    
            reset();
        }

        if (gameOver)
            return;
        
        if (Star.CollideRocket(rocket))
        {
            gameOver = true;
        }
//add or modify.  Code already given to you.
//                Determine if the rocket has collided with a planet and
//                add to score if there was a collision.
        score += Planet.CollideRocket(rocket);

//add or modify.  Code already given to you.
//                Determine if a missile has collided with a star and
//                add to score if there was a collision.
           score += Missile.CollideStars();
         
            Missile.CollideStars();

        if (timeCount % (int)(4*frameRate) == ((int)(4*frameRate)-1))
        {     
            Star.Create(rocket.getXSpeed());
        }       
//add or modify.  Create a planet every 5 seconds.
 
        if (timeCount % (int)(5*frameRate) == ((int)(5*frameRate)-1))
        {     
            Planet.Create(rocket.getXSpeed());
        }  
//add or modify.  Move the planets and see if there is a game over.
if ( Planet.Animate(rocket.getXSpeed())){
    gameOver = true;
    }
       
        Star.Animate(rocket.getXSpeed());
        Missile.Animate();
        rocket.Animate();
        timeCount++;

    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }

}

///////////////////////////////////////////////////////////////

class Star {
    public static Image image;
    private static ArrayList<Star> stars = new ArrayList<Star>();    
     private static int numPoints;

    private int xPos;
    private int yPos;
    private int size;

    public static void Reset() {
        stars.clear();
    }
    
    public static void Create(int xspeed)
    {
        if (xspeed > 0) {
            Star star = new Star(Window.getWidth2());
            stars.add(star);        
        }
        else if (xspeed < 0) {
            Star star = new Star(0);
            stars.add(star);        
        }
    }    
    
    public static void Draw() {    
        for (Star star : stars)
            Drawing.drawImage(image,Window.getX(star.xPos),Window.getYNormal(star.yPos),0.0,star.size,star.size );
//            Drawing.drawCircle(Window.getX(star.xPos),Window.getYNormal(star.yPos),0.0,star.size,star.size,Color.yellow );
    }
        
    public static void Animate(int xSpeed) {
        for (int i=0;i<stars.size();i++) {
            stars.get(i).xPos -= xSpeed;
            
//Remove the star when it goes beyond the right or left border.
            if (stars.get(i).xPos < 0) {
                stars.remove(i);
                i--;
            }
            else if (stars.get(i).xPos > Window.getWidth2()) {
                stars.remove(i);
                i--;
            }
        }
    }    
    
    public static boolean CollideRocket(Rocket rocket) {
        for (Star star : stars) {
            if (star.xPos + star.size*10 > rocket.getXPos() && star.xPos - star.size*10 < rocket.getXPos() &&
            star.yPos + star.size*10 > rocket.getYPos() && star.yPos - star.size*10 < rocket.getYPos())
                return (true);
        }
        return (false);
    }
    
    public static Star CollideMissile(Missile missile) {
        for (Star star : stars) {
            if (star.xPos + star.size*10 > missile.getXPos() && star.xPos - star.size*10 < missile.getXPos() &&
            star.yPos + star.size*10 > missile.getYPos() && star.yPos - star.size*10 < missile.getYPos())
                return (star);
        }
        return (null);
    }
    
    public static void RemoveStar(Star star) {
        stars.remove(star);
    }
            
    public Star(int _xPos) {        
        xPos = _xPos;
        yPos = (int)(Math.random()*Window.getHeight2());
        size = (int)(Math.random()*3+1);
    }    
     public int getSize() {
        return (size);
    }

//add or modify.  Create a method named getPoints and have it return the 
//                number of points for the given star depending on if it is
//                big, medium, or small.
    public int getPoints(){
        return(4-size);
    }
}

///////////////////////////////////////////////////////////////
//add or modify.  Uncomment and add the missing code for the Planet class.

class Planet {
       private static ArrayList<Planet> planets = new ArrayList<Planet>(); 
       
    private int xPos;
    private int yPos;
    private int randColor;

    public static void Reset() {
       planets.clear();
    }
    
    public static void Create(int xspeed)
    {
     if (xspeed > 0) {
            Planet planet = new Planet(Window.getWidth2());
            planets.add(planet);        
        }
        else if (xspeed < 0) {
            Planet planet = new Planet(0);
            planets.add(planet);        
        }
    }    
    
    public static void Draw() {
          for (int i=0;i<planets.size();i++) {
            if (planets.get(i).randColor == 0) {
         Drawing.drawCircle(Window.getX(planets.get(i).xPos),Window.getYNormal(planets.get(i).yPos),0.0,2,2,Color.RED );
            }
            else if(planets.get(i).randColor == 1)  {
                Drawing.drawCircle(Window.getX(planets.get(i).xPos),Window.getYNormal(planets.get(i).yPos),0.0,2,2,Color.blue );
            }
          }
      } 
  public static boolean Animate(int xSpeed){
       for (int i=0;i<planets.size();i++) {
            planets.get(i).xPos -= xSpeed;
            if (planets.get(i).xPos < 0) {
                planets.remove(i);
                i--;
                 return(true);
            }
            else if (planets.get(i).xPos > Window.getWidth2()) {
                planets.remove(i);
                i--;
                return(true);
            }
        }
       return(false);
  }
   public static int CollideRocket(Rocket rocket) {
       int points = 0;
        for (int i=0;i<planets.size();i++) {
            if (planets.get(i).xPos +15 > rocket.getXPos() && planets.get(i).xPos -15 < rocket.getXPos() &&
            planets.get(i).yPos+15>rocket.getYPos() && planets.get(i).yPos -15< rocket.getYPos()){
                if (planets.get(i).randColor == 0 ){
                    planets.remove(i);
                    points = 2;
                }
            else if(planets.get(i).randColor == 1) {
                 planets.remove(i);
                points = 5;
                }
            }
        }
        return(points);
    }
  
      
    public Planet(int _xPos) {  
        xPos = _xPos;
        yPos = (int)(Math.random()*Window.getHeight2());
        randColor = (int)(Math.random()*2);
        
    }
     public int getXPos() {
        return (xPos);
    }
      public int getYPos() {
        return (yPos);
    }
}

///////////////////////////////////////////////////////////////

class Missile {
    private static ArrayList<Missile> missiles = new ArrayList<Missile>();     
//add or modify.  Variable already added for you.  Limits the number of active missiles.
    private static int numActiveMissiles = 4;
    
    private int xPos;
    private int yPos;
    private int xSpeed;

    public static void Reset() {
        missiles.clear();
    }
    
    public static void Create(boolean _faceRight,int _xPos,int _yPos)
    {
//add or modify.  Limit the number of active missiles.
        if (missiles.size() < numActiveMissiles){
        if (_faceRight) {
            Missile missile = new Missile(4,_xPos,_yPos);
            missiles.add(missile); 
        }
        else {
            Missile missile = new Missile(-4,_xPos,_yPos);
            missiles.add(missile);  
           }
        }
    } 
//add or modify.  Determine how many points to return based on missiles colliding with stars.
    public static int CollideStars() {
        Star star = null;
        int points = 0;
        for (int i=0;i<missiles.size();i++) {
            if ((star = Star.CollideMissile(missiles.get(i))) != null) {
                missiles.remove(i);
                i--;
                points = star.getPoints();
            }
            if (star != null)
                Star.RemoveStar(star);
        } 
        return(points);
    }
    
    public static void Draw() {    
        for (Missile missile : missiles)
            Drawing.drawCircle(Window.getX(missile.xPos),Window.getYNormal(missile.yPos),0.0,2.0,0.4,Color.yellow );
    }
        
    public static void Animate() {
        for (int i=0;i<missiles.size();i++) {
            missiles.get(i).xPos += missiles.get(i).xSpeed;
//Remove the missile when it goes past the left or right border.
            if (missiles.get(i).xPos < 0 || missiles.get(i).xPos > Window.getWidth2())
            {
                missiles.remove(i);
                i--;
            }
        }
    }    
        
    public Missile(int _xSpeed,int _xPos,int _yPos) { 
        xSpeed = _xSpeed;
        xPos = _xPos;
        yPos = _yPos;
    }    
    public int getXPos() {
        return (xPos);
    }
    public int getYPos() {
        return (yPos);
    }    

}
///////////////////////////////////////////////////////////////
class Rocket {
    public static Image image;
    private static int maxSpeed = 10;
    private int xPos;
    private int yPos;
    private int xSpeed;
    private int ySpeed;
    private boolean faceRight;
    public Rocket() {      
//Initially place the rocket in the center.        
        xPos = Window.getWidth2()/2;
        yPos = Window.getHeight2()/2;
        ySpeed = 0;
        faceRight = true;
    }
    
    public void Draw() {        
        if (faceRight)
        {
            Drawing.drawImage(image,Window.getX(xPos),Window.getYNormal(yPos),0.0,1.0,1.0 );
//            Drawing.drawRocket(Window.getX(xPos),Window.getYNormal(yPos),0.0,1.0,0.7,Color.red );
            
        }
        else
        {
            Drawing.drawImage(image,Window.getX(xPos),Window.getYNormal(yPos),0.0,-1.0,1.0 );
//            Drawing.drawRocket(Window.getX(xPos),Window.getYNormal(yPos),0.0,-1.0,0.7,Color.red );
        }   
    }
    public boolean getFaceRight() {
        return (faceRight);
    }
    public int getXSpeed() {
        return (xSpeed);
    }
    public int getXPos() {
        return (xPos);
    }
    public int getYPos() {
        return (yPos);
    }
    public void IncreaseXSpeed(int speedInc) {
        xSpeed += speedInc;
        if (xSpeed > 0)
            faceRight = true;
        else if (xSpeed < 0)
            faceRight = false;
//Limit the xSpeed of the rocket.        
        if (xSpeed > maxSpeed)
            xSpeed = maxSpeed;
        else if (xSpeed < -maxSpeed)
            xSpeed = -maxSpeed;
    }
    public void IncreaseYSpeed(int speedInc) {
        ySpeed += speedInc;
    }
    public void Animate() {
        yPos += ySpeed;
        
//Prevent the rocket from going beyond the top or bottom border.
//add or modify.  Have the rocket bounce off the boundary.    
        if (yPos > Window.getHeight2())
        {
            yPos = Window.getHeight2();
            ySpeed = -ySpeed;
        }
        if (yPos < 0)
        {
            yPos = 0;
            ySpeed = -ySpeed;            
        }        
    }
}


////////////////////////////////////////////////////////////////////////////

class Window {
    private static final int XBORDER = 60;
    
//    private static final int YBORDER = 20;
    
    private static final int TOP_BORDER = 40;
    private static final int BOTTOM_BORDER = 20;
    
    private static final int YTITLE = 30;
    private static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + 600;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 600;
    static int xsize = -1;
    static int ysize = -1;


/////////////////////////////////////////////////////////////////////////
    public static int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public static int getY(int y) {
//        return (y + YBORDER + YTITLE );
        return (y + TOP_BORDER + YTITLE );
        
    }

    public static int getYNormal(int y) {
//          return (-y + YBORDER + YTITLE + getHeight2());
      return (-y + TOP_BORDER + YTITLE + getHeight2());
        
    }
    
    public static int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public static int getHeight2() {
//        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
        return (ysize - (BOTTOM_BORDER + TOP_BORDER) - WINDOW_BORDER - YTITLE);
    }    
}

class Drawing {
    private static Graphics2D g;
    private static RocketTest mainClassInst;

    public static void setDrawingInfo(Graphics2D _g,RocketTest _mainClassInst) {
        g = _g;
        mainClassInst = _mainClassInst;
    }
////////////////////////////////////////////////////////////////////////////
    public static void drawRocket(int xpos,int ypos,double rot,double xscale,double yscale,Color color)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(color);
        g.fillRect(-10,-10,20,20);
        int xvals[] = {10,10,20};
        int yvals[] = {10,-10,0};
        g.fillPolygon(xvals,yvals,xvals.length);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
////////////////////////////////////////////////////////////////////////////
    public static void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale,Color color)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(color);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public static void drawImage(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = image.getWidth(mainClassInst);
        int height = image.getHeight(mainClassInst);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,mainClassInst);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }    
}

 

 

 

 

 

