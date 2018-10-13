package tws.keeper.solution;

import static tws.keeper.model.Action.GO_DOWN;
import static tws.keeper.model.Action.GO_LEFT;
import static tws.keeper.model.Action.GO_RIGHT;
import static tws.keeper.model.Action.GO_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tws.keeper.model.Action;
import tws.keeper.model.Keeper;
import tws.keeper.model.Observable;
import tws.keeper.model.Position;

public class KeeperAI implements Keeper {

	private static final List<Action> availableActions = Arrays.asList(GO_DOWN, GO_LEFT, GO_RIGHT, GO_UP);

	ExploratoryAlgorithm observableUtils;
	List<Position> openPositions;
	List<Action> pathToDoor;

	public KeeperAI() {
		openPositions = new ArrayList<>();
		pathToDoor = new ArrayList<>();
	}

	/**
	 * This Keeper Artificial Intelligence simply acts randomly
	 *
	 * @param maze
	 * @return
	 */
	public Action act(Observable maze) {
		if (observableUtils == null) {
			observableUtils = new ExploratoryAlgorithm(maze, availableActions);
		}

		// Storing the keeper position
		observableUtils.addVisitedPosition();

		// Going direct to the door if door position is known and keys were found
		if (observableUtils.doorPositionIsKnown() && observableUtils.allKeysAreFound()) {
			if (pathToDoor.isEmpty()) {
				pathToDoor = (new StarAlgorithm(observableUtils)).createPath();
			}

			if (pathToDoor.isEmpty()) {
				return Action.DO_NOTHING;
			}

			return pathToDoor.remove(0);
		}

		// Getting the next action based on current knowledge
		return observableUtils.getNextAction();
	}

}