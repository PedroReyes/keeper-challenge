package tws.keeper.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import tws.keeper.model.Action;
import tws.keeper.model.Position;

/**
 * Implementation of the well-known pathfinding A* algorithm
 * 
 * @author pedro
 *
 */
public class StarAlgorithm {

	private List<Node> visitedNodes;
	private List<Node> open;
	private List<Node> closed;
	private ExploratoryAlgorithm observableUtils;

	private static final String NO_PATH_FOUND = "No path to the target was found";
	private static final String NO_NODES_VISITED = "None visited nodes";

	public StarAlgorithm(ExploratoryAlgorithm mazeUtils) {
		this.observableUtils = mazeUtils;
	}

	/**
	 * Given the current keeper position, the door position and the positions
	 * already visited in the map. We create a list of actions for reaching the door
	 * position with the current knowledge of the map
	 * 
	 * @param keeperPosition
	 * @param pheromoneTrack
	 * @param doorPosition
	 * @return
	 */
	public List<Action> createPath() {
		// Start node
		Node startNode = new Node(observableUtils.getMaze().getKeeperPosition(), Action.DO_NOTHING);

		// Target node
		Node targetNode = new Node(observableUtils.getDoorPosition(), Action.DO_NOTHING);

		// Converting pheromoneTrack into a list of Node objects
		visitedNodes = observableUtils.getVisitedPositions().stream()
				.map(position -> new Node(position, Action.DO_NOTHING)).collect(Collectors.toList());

		// The set of nodes to be evaluated
		open = new ArrayList<Node>();

		// Initially only the start point is the one that can be evaluated
		open.add(startNode);

		// The set of nodes already evaluated
		closed = new ArrayList<Node>();

		// Starting the search
		while (!open.isEmpty()) {
			// current = node in OPEN with the lowest f_cost && remove current from OPEN
			final Node currentNode;
			currentNode = pollLowestHeuristicNode();

			// add current to CLOSED
			closed.add(currentNode);

			if (currentNode.equals(targetNode)) {
				// Path has been found
				return extractPath(startNode, currentNode);
			}

			// Get current' neighbours
			List<Node> nodes = possibleActions(currentNode, observableUtils.getAvailableActions());

			nodes.stream().forEach(neighbourNode -> {
				// Go to next if neighbour is not traversal or is in the closed list
				// All the possible neighbour in our case are traversal since we only
				// take into consideration nodes that the keeper already visited
				if (closed.contains(neighbourNode)) {
					return;
				}

				// Calculating new G cost for going to the neighbour node
				double newMovementCostToNeighbour = currentNode.getgCost()
						+ calculateDistance(currentNode, neighbourNode);
				if (!open.contains(neighbourNode)) {
					// Calculate costs
					neighbourNode.setgCost(newMovementCostToNeighbour);
					neighbourNode.sethCost(calculateDistance(neighbourNode, targetNode));

					// Set parent to node
					neighbourNode.setParent(currentNode);

					// If neighbour is not in open, add it
					if (!open.contains(neighbourNode)) {
						open.add(neighbourNode);
					}
				}
			});
		}

		throw new IllegalStateException(NO_PATH_FOUND);
	}

	/**
	 * Return the actions to reach the target no from the start node
	 * 
	 * @param startNode
	 * @param targetNode
	 * @return
	 */
	private List<Action> extractPath(Node startNode, Node targetNode) {
		List<Action> actionsToGetToTargetNode = new ArrayList<>();
		Node currentNode = targetNode;

		while (!currentNode.equals(startNode)) {
			actionsToGetToTargetNode.add(currentNode.getActionAssociated());
			currentNode = currentNode.getParent();
		}

		Collections.reverse(actionsToGetToTargetNode);
		return actionsToGetToTargetNode;
	}

	/**
	 * Given the current node and the already known visited nodes we return those
	 * nodes that we can get to with the possible actions (RIGHT, LEFT, UP, DOWN)
	 * 
	 * @param current
	 * @return
	 */
	private List<Node> possibleActions(Node current, List<Action> availableActions) {
		if (visitedNodes == null) {
			throw new IllegalStateException(NO_NODES_VISITED);
		}

		List<Node> possibleActions = new ArrayList<Node>();

		availableActions.stream().forEach(action -> updatePossibleActions(current, possibleActions, action));

		return possibleActions;
	}

	/**
	 * Update the list of possible actions to be
	 * 
	 * @param current
	 * @param possibleActions
	 * @param action
	 */
	private void updatePossibleActions(Node current, List<Node> possibleActions, Action action) {
		Position positionAfterAction = MazeUtils.positionAfterAction(action, current.getPosition());
		boolean positionAfterActionVisited = visitedNodes.stream().map(Node::getPosition)
				.anyMatch(positionAfterAction::equals);
		if (positionAfterActionVisited) {
			possibleActions.add(new Node(positionAfterAction, action));
		}
	}

	/**
	 * Each time you poll a node in this method, the list will be re-ordered
	 * 
	 * @return
	 */
	private Node pollLowestHeuristicNode() {
		Node current;
		open = open.stream().sorted((a, b) -> {
			int compareWithF = a.getfCost().compareTo(b.getfCost());

			if (compareWithF == 0) {
				return a.gethCost().compareTo(b.gethCost());
			}

			return compareWithF;
		}).collect(Collectors.toList());
		current = open.get(0);
		open.remove(0);
		return current;
	}

	/**
	 * Distance from a to b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private double calculateDistance(Node a, Node b) {
		double distX = Math.abs(a.getPosition().getHorizontal() - b.getPosition().getHorizontal());
		double distY = Math.abs(a.getPosition().getVertical() - b.getPosition().getVertical());

		return distX + distY;
	}

}
