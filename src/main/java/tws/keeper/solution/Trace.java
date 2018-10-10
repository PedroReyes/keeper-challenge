package tws.keeper.solution;

import tws.keeper.model.Position;

public class Trace {
	private final Position position;
	private double pheromoneIntesity;
	private int iteration;

	public Trace(Position position, double pheromoneIntesity) {
		this.position = position;
		this.pheromoneIntesity = pheromoneIntesity;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public Position getPosition() {
		return position;
	}

	public double getPheromoneIntesity() {
		return pheromoneIntesity;
	}

	public void setPheromoneIntesity(double pheromoneIntesity) {
		this.pheromoneIntesity = pheromoneIntesity;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return position.equals(((Trace) obj).getPosition());
	}

	@Override
	public String toString() {
		return position.toString() + " -> " + pheromoneIntesity;
	}

}
