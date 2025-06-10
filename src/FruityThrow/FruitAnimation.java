package FruityThrow;

public class FruitAnimation {
    private double vx;
    private double vy;
    private double gravity;
    private double time;
    private double initialX;
    private double initialY;

    public FruitAnimation(double angleDegree, double velocity, double gravity, double initialX, double initialY) {
        this.gravity = gravity;
        this.initialX = initialX;
        this.initialY = initialY;
        
        double angleRad = Math.toRadians(angleDegree);
        this.vx = velocity * Math.cos(angleRad);
        this.vy = velocity * Math.sin(angleRad);
        
    }

    public void updateTime(double deltaTime, double simulationSpeed) {
        time += deltaTime * simulationSpeed;
    }

    public double[] getPosition() {
        double newX = initialX + vx * time;
        double newY = initialY - (vy * time - 0.5 * gravity * time * time);
        double[] resultPosition = new double[] {newX, newY};
        return resultPosition;
    }

    public boolean isLanded(double groundY) {
        return getPosition()[1] > groundY;
    }
}


/******JULIA STEFANIAK******/