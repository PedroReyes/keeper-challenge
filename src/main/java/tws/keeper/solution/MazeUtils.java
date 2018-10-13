package tws.keeper.solution;

import java.util.ArrayList;
import java.util.List;

import tws.keeper.model.Action;
import tws.keeper.model.Cell;
import tws.keeper.model.Observable;
import tws.keeper.model.Position;

/**
 * This class contains general purpose methods that could be used for different
 * algorithms to be implemented by KeeperAI. With this we can test the correct
 * behavior of this method and we extract general purpose logic for being
 * accessible
 * 
 * @author pedro
 *
 */
public class MazeUtils {

	private static final String IMPOSSIBLE_MOVEMENT = "You can't go with any action from %s to %s";

	/**
	 * Returns the action, if possible, from going from position from to position
	 * to. If no action is possible to do this movement, an exception will be thrown
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static Action actionForGoing(Position from, Position to, List<Action> availableActions) {
		for (Action action : availableActions) {
			if (positionAfterAction(action, from).equals(to)) {
				return action;
			}
		}

		throw new IllegalStateException(String.format(IMPOSSIBLE_MOVEMENT, from, to));
	}

	/**
	 * Gives the position after action is applied
	 * 
	 * @param action
	 * @param keeperPosition
	 * @return
	 */
	public static Position positionAfterAction(Action action, Position keeperPosition) {
		switch (action) {
		case GO_DOWN:
			return new Position(keeperPosition.getVertical() + 1, keeperPosition.getHorizontal());
		case GO_UP:
			return new Position(keeperPosition.getVertical() - 1, keeperPosition.getHorizontal());
		case GO_LEFT:
			return new Position(keeperPosition.getVertical(), keeperPosition.getHorizontal() - 1);
		case GO_RIGHT:
			return new Position(keeperPosition.getVertical(), keeperPosition.getHorizontal() + 1);
		default:
		case DO_NOTHING:
			return keeperPosition;
		}
	}

	/**
	 * If we have beside to the keeper an unknown door position or a key, then that
	 * will be the most probable action. Being the unknown door position first.
	 * 
	 * @param maze
	 * @param availableActions
	 * @return
	 */
	public static Action highestProbableAction(Observable maze, List<Action> availableActions, Position doorPosition) {
		// @formatter:off
		List<Action> possibleActions = possibleActions(maze, availableActions);
		
		// Checking if there is a door beside the keeper and the door position is not known
		Action highestProbableAction = possibleActions.stream()
				.filter(action -> actionLeadsTo(maze, action, Cell.DOOR) && doorPosition == null)
				.findFirst().orElse(null);

		// Checking if there is a key beside the keeper
		if (highestProbableAction == null && maze.getKeysFound() != maze.getTotalNumberOfKeys()) {
			return possibleActions.stream()
					.filter(action -> actionLeadsTo(maze, action, Cell.KEY))
					.findFirst().orElse(null);
		}
		
		return highestProbableAction;
		// @formatter:on
	}

	/**
	 * Every action that leads to a wall is not a possible action
	 * 
	 * @param maze
	 * @return
	 */
	public static List<Action> possibleActions(Observable maze, List<Action> availableActions) {
		// @formatter:off
		List<Action> actions = new ArrayList<>();

		for (Action action : availableActions) {
			if (actionLeadsTo(maze, action, Cell.DOOR) 
					|| actionLeadsTo(maze, action, Cell.KEY)
					|| actionLeadsTo(maze, action, Cell.PATH)) {
				actions.add(action);
			}
		}

		return actions;
		// @formatter:on
	}

	/**
	 * Return, if possible, the position of the door searching in the
	 * 
	 * @param maze
	 * @return
	 */
	public static Position getDoorPosition(Observable maze, List<Action> availableActions) {
		for (Action action : availableActions) {
			if (actionLeadsTo(maze, action, Cell.DOOR)) {
				return positionAfterAction(action, maze.getKeeperPosition());
			}
		}

		return null;
	}

	/**
	 * Possible next position for current keeper position
	 * 
	 * @return
	 */
	public static List<Position> possibleNextPositions(Observable maze, List<Action> availableActions,
			Position doorPosition) {
		List<Action> actions = possibleActions(maze, availableActions);
		List<Position> positions = new ArrayList<>();
		actions.forEach(action -> positions.add(positionAfterAction(action, maze.getKeeperPosition())));
		return positions;
	}

	/**
	 * Returns if the action leads to the cell type specified
	 * 
	 * @param maze
	 * @param action
	 * @param cell
	 * @return
	 */
	private static boolean actionLeadsTo(Observable maze, Action action, Cell cell) {
		switch (action) {
		case GO_DOWN:
			return cell.equals(maze.lookDown());
		case GO_UP:
			return cell.equals(maze.lookUp());
		case GO_LEFT:
			return cell.equals(maze.lookLeft());
		default:
		case GO_RIGHT:
			return cell.equals(maze.lookRight());
		}
	}

}
