package tws.keeper.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import tws.keeper.model.Action;
import tws.keeper.model.Observable;
import tws.keeper.model.Position;

/**
 * This class contains the exploratory mode of the keeper. The implementation is
 * based on a stack that stores the current path and the way back to other
 * alternative paths. We know if a path was not visited storing the already
 * visited nodes in a separate list.
 * 
 * @author pedro
 *
 */
public class ExploratoryAlgorithm {

	private Position doorPosition;
	private Observable maze;
	private List<Action> availableActions;
	private List<Position> visitedPositions;
	private Stack<Position> openPath;

	private static final String MAP_COMPLETELY_EXPLORED_BUT_NOT_SUCCEED = "All the nodes have been visited. However, the game was not successful.";

	private ExploratoryAlgorithm() {
		doorPosition = null;
		maze = null;
	}

	public ExploratoryAlgorithm(Observable maze, List<Action> availableActions) {
		this();
		this.visitedPositions = new ArrayList<>();
		this.maze = maze;
		this.availableActions = availableActions;
		this.openPath = new Stack<>();
		this.doorPosition = null;
	}

	/**
	 * Based on a stack this algorithm stores the different positions visited and
	 * the path that is currently on going. When arriving to a point in which there
	 * is no possible actions, the algorithm will return the action from coming
	 * back. Otherwise, the algorithm will return randomly one of the possible
	 * actions that we have from the current positions and that do not move us into
	 * an already visited position.
	 * 
	 * @return
	 */
	public Action getNextAction() {
		// Checking if it is an already visited position
		List<Position> possibleNextPositions = MazeUtils.possibleNextPositions(maze, availableActions, doorPosition);
		possibleNextPositions.removeAll(visitedPositions);

		if (possibleNextPositions.isEmpty()) {
			if (openPath.isEmpty()) {
				throw new IllegalStateException(MAP_COMPLETELY_EXPLORED_BUT_NOT_SUCCEED);
			}

			// Going on step back in the stack
			return MazeUtils.actionForGoing(maze.getKeeperPosition(), openPath.pop(), availableActions);
		} else {
			// Adding the position to the stack
			openPath.push(maze.getKeeperPosition());

			// @formatter:off 
			// Just taking into account those actions that were not visited yet
			List<Action> possibleActions = MazeUtils.possibleActions(maze, availableActions).stream()
					.filter(action -> !visitedPositions.contains(MazeUtils.positionAfterAction(action, maze.getKeeperPosition())))
					.collect(Collectors.toList());
			// @formatter:on 

			// Storing door position if we can
			if (!doorPositionIsKnown()) {
				doorPosition = MazeUtils.getDoorPosition(maze, availableActions);
			}

			// Getting highest probable action if any
			Action highestProbableAction = MazeUtils.highestProbableAction(maze, availableActions, doorPosition);
			if (highestProbableAction != null) {
				return highestProbableAction;
			}

			// Choosing one action randomly
			return possibleActions.get(ThreadLocalRandom.current().nextInt(possibleActions.size()));
		}
	}

	public boolean doorPositionIsKnown() {
		return doorPosition != null;
	}

	public boolean allKeysAreFound() {
		return maze.getKeysFound() == maze.getTotalNumberOfKeys();
	}

	public void addVisitedPosition() {
		visitedPositions.add(maze.getKeeperPosition());
	}

	public Observable getMaze() {
		return maze;
	}

	public Position getDoorPosition() {
		return doorPosition;
	}

	public List<Position> getVisitedPositions() {
		return visitedPositions;
	}

	public List<Action> getAvailableActions() {
		return availableActions;
	}

}
