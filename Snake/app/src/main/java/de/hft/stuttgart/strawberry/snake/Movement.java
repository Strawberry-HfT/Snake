package de.hft.stuttgart.strawberry.snake;

/**
 * Created by Juliano on 08.04.2015.
 * Hilfsklasse zum Speichern der Bewegung
 */
public class Movement {

    // Richtungen
    private boolean up = false;
    private boolean down = false;
    private boolean right = false;
    private boolean left = false;

    // Constructor
    public Movement(){
        // Standardkonstruktor
    }

    // Setter und Getter
    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        if(!this.down){
            this.up = up;
            // Wenn up == true
            if(up){
                this.down = false;
                this.right = false;
                this.left = false;
            }
        }
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        if(!this.up){
            this.down = down;
            if(down){
                this.up = false;
                this.right = false;
                this.left = false;
            }
        }

    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        if(!isLeft()) {
            this.right = right;
            if (right) {
                this.up = false;
                this.down = false;
                this.left = false;
            }
        }
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        if(!isRight()){
            this.left = left;
            if(left){
                this.up = false;
                this.down = false;
                this.right = false;
            }
        }
    }
}
