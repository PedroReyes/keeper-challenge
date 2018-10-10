package tws.keeper.solution;

import static tws.keeper.model.Action.GO_DOWN;
import static tws.keeper.model.Action.GO_LEFT;
import static tws.keeper.model.Action.GO_RIGHT;
import static tws.keeper.model.Action.GO_UP;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import tws.keeper.model.Action;
import tws.keeper.model.Keeper;
import tws.keeper.model.Observable;

public class KeeperAI implements Keeper {

	public static final List<Action> availableActions = Arrays.asList(GO_DOWN, GO_LEFT, GO_RIGHT, GO_UP);

	/**
	 * This Keeper Artificial Inteligence simply acts randomly
	 *
	 * @param maze
	 * @return
	 */
	public Action act(Observable maze) {
		return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
	}

}