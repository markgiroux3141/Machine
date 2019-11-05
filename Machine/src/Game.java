import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JFrame implements ActionListener
{
    private static final int WIDTH = 1550;
    private static final int HEIGHT = 830;
    private static final int MACHINE_SCALE = 400;

    private GamePanel gamePanel = new GamePanel();
    private JButton startButton = new JButton("Start");
    private JButton quitButton = new JButton("Quit");
    private JButton pauseButton = new JButton("Pause");
    private boolean running = false;
    private boolean paused = false;
    private int fps = 60;
    private int frameCount = 0;

    public Game()
    {
        super("Machine Simulation");
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1,2));
        p.add(startButton);
        p.add(pauseButton);
        p.add(quitButton);
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(p, BorderLayout.SOUTH);
        setSize(WIDTH, HEIGHT);

        startButton.addActionListener(this);
        quitButton.addActionListener(this);
        pauseButton.addActionListener(this);
    }

    public static void main(String[] args)
    {
        Game glt = new Game();
        glt.setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        Object s = e.getSource();
        if (s == startButton)
        {
            running = !running;
            if (running)
            {
                startButton.setText("Stop");
                runGameLoop();
            }
            else
            {
                startButton.setText("Start");
            }
        }
        else if (s == pauseButton)
        {
            paused = !paused;
            if (paused)
            {
                pauseButton.setText("Unpause");
            }
            else
            {
                pauseButton.setText("Pause");
            }
        }
        else if (s == quitButton)
        {
            System.exit(0);
        }
    }

    //Starts a new thread and runs the game loop in it.
    public void runGameLoop()
    {
        Thread loop = new Thread()
        {
            public void run()
            {
                gameLoop();
            }
        };
        loop.start();
    }

    //Only run this in another Thread!
    private void gameLoop()
    {
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 240.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();

        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 240;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (running)
        {
            double now = System.nanoTime();
            int updateCount = 0;

            if (!paused)
            {
                //Do as many game updates as we need to, potentially playing catchup.
                while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
                {
                    updateGame();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
                {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }

                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
                drawGame(interpolation);
                lastRenderTime = now;

                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime)
                {
                    System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                }

                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
                {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {Thread.sleep(1);} catch(Exception e) {}

                    now = System.nanoTime();
                }
            }
        }
    }

    private void updateGame()
    {
        gamePanel.update();
    }

    private void drawGame(float interpolation)
    {
        gamePanel.setInterpolation(interpolation);
        gamePanel.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    private class GamePanel extends JPanel
    {
        Simulator simulator;
        float interpolation;

        public GamePanel()
        {
            simulator = new Simulator();
        }

        public void setInterpolation(float interp)
        {
            interpolation = interp;
        }

        public void update()
        {
            simulator.runSimulation(interpolation);
        }

        public void paintComponent(Graphics g)
        {
            g.clearRect(0,0, Game.WIDTH, Game.HEIGHT);
            simulator.getCompressor().drawCompressor(g, 50,100, Game.MACHINE_SCALE);
            simulator.getMotor().drawMotor(g, 70,600, Game.MACHINE_SCALE);
            simulator.getChamber().drawChamber(g, 350,50,Game.MACHINE_SCALE);
            simulator.getOutputChamber().drawChamber(g, 400,350, Game.MACHINE_SCALE);
            simulator.getHeatExchanger().drawHeatExchanger(g, 700, 400,Game.MACHINE_SCALE);
            simulator.getInputCoolantChamber().drawChamber(g,650,50,Game.MACHINE_SCALE);
            simulator.getOutputContentChamber().drawChamber(g,1100,300,Game.MACHINE_SCALE);
            simulator.getOutputCoolantChamber().drawChamber(g,650,600,Game.MACHINE_SCALE);
            simulator.getExpander().drawExpander(g,1300,300,Game.MACHINE_SCALE);
            simulator.getExpanderOutputGasChamber().drawChamber(g,1300,100,Game.MACHINE_SCALE);
            simulator.getExpanderOutputLiquidChamber().drawChamber(g,1300,500,Game.MACHINE_SCALE);
            simulator.getHeatExchangerGateChamber().drawChamber(g, 900,300,Game.MACHINE_SCALE);

            frameCount++;
        }
    }

}