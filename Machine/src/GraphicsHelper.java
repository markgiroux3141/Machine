import java.awt.Graphics;
import java.awt.Color;

public class GraphicsHelper {
    public static void drawCircle(Graphics g,int xPos, int yPos, int radius){
        g.drawOval(xPos - radius, yPos - radius, radius * 2, radius * 2);
    }

    public static void fillCircle(Graphics g, int xPos, int yPos, int radius, Color color){
        g.setColor(color);
        g.fillOval(xPos - radius, yPos - radius, radius * 2, radius * 2);
        g.setColor(new Color(0,0,0));
    }

    public static void fillCoil(Graphics g, int xPos, int yPos, Color gasColor, Color coolantColor, double pipeWidth, double pipeLength, double scale){
        double scaledPipeWidth = pipeWidth * scale;
        double numberOfPipes = Math.floor(Math.sqrt(pipeLength/(2*pipeWidth)));
        double coilHeight = pipeWidth * numberOfPipes;
        double scaledCoilHeight = coilHeight * scale;
        g.setColor(coolantColor);
        g.fillRect(xPos - (int)scaledPipeWidth, yPos, (int)scaledCoilHeight + (int)scaledPipeWidth*2, (int)scaledCoilHeight + (int)scaledPipeWidth*2);
        g.setColor(gasColor);

        for(int i=0;i< (numberOfPipes/2);i++){
            g.fillArc(xPos + (int)(i * scaledPipeWidth * 2),yPos + (int)(scaledPipeWidth * 0.25),(int)(scaledPipeWidth * 1.5),(int)(scaledPipeWidth * 1.5),0,180);
            g.fillArc(xPos + (int)scaledPipeWidth + (int)(i * scaledPipeWidth * 2),yPos + (int)scaledCoilHeight + (int)(scaledPipeWidth * 0.25),(int)(scaledPipeWidth * 1.5),(int)(scaledPipeWidth * 1.5),180,180);
        }

        for(int i=0;i<numberOfPipes;i++){
            g.fillRect(xPos + (int)(i * scaledPipeWidth),yPos + (int)scaledPipeWidth, (int)(scaledPipeWidth/2), (int)scaledCoilHeight);
        }

        g.setColor(coolantColor);

        for(int i=0;i< (numberOfPipes/2);i++){
            g.fillArc(xPos + (int)(scaledPipeWidth/2) + (int)( i * (scaledPipeWidth * 2)) ,yPos + (int)(scaledPipeWidth / 1.25),(int)(scaledPipeWidth/2),(int)(scaledPipeWidth/2),0,180);
            g.fillArc(xPos + (int)scaledPipeWidth + (int)(scaledPipeWidth/2) + (int)( i * (scaledPipeWidth * 2)),yPos + (int)scaledCoilHeight + (int)(scaledPipeWidth / 1.4),(int)(scaledPipeWidth/2),(int)(scaledPipeWidth/2),180,180);
        }

        g.setColor(new Color(0,0,0));
    }

    public static Color getTemperatureGradient(double minTemp, double maxTemp, double temp){
        double halfTemp = (maxTemp + minTemp)/2;
        if(temp <= halfTemp){
            int tempCol = (int)Math.min((temp - minTemp) * (255/halfTemp), 255);
            return new Color(tempCol,tempCol,255);
        }else{
            int tempCol = (int)Math.min((temp - halfTemp) * (255/halfTemp), 255);
            return new Color(255,255 - tempCol, 255 - tempCol);
        }
    }
}