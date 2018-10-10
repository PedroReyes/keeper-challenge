package tws.keeper.solution;

import tws.keeper.model.Action;
import tws.keeper.model.Position;

public class Node {
	private final Position position;
	private Action actionAssociated;
	private Node parent;
	private Double gCost = 0.0; // Cost from starting node to current node
	private Double hCost = 0.0; // Cost from this node to final node

	public Node(Position position, Action actionAssociated) {
		this.position = position;
		this.actionAssociated = actionAssociated;
	}

	public Action getActionAssociated() {
		return actionAssociated;
	}

	public void setActionAssociated(Action actionAssociated) {
		this.actionAssociated = actionAssociated;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Double getgCost() {
		return gCost;
	}

	public void setgCost(Double gCost) {
		this.gCost = gCost;
	}

	public Double gethCost() {
		return hCost;
	}

	public void sethCost(Double hCost) {
		this.hCost = hCost;
	}

	public Position getPosition() {
		return position;
	}

	public Double getfCost() {
		return gCost + hCost;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return position.equals(((Node) obj).getPosition());
	}

	@Override
	public String toString() {
		return position.toString() + " -> " + actionAssociated.name() + " -> f = " + gCost + " + " + hCost + " = "
				+ (gCost + hCost) + "\n";
	}

}
