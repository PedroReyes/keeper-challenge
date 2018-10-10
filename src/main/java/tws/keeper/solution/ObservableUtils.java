package tws.keeper.solution;

import tws.keeper.model.Action;
import tws.keeper.model.Position;

public class ObservableUtils {

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
	 * Given an action, this method will return the inverse. That is, if you provide
	 * RIGHT it will return LEFT
	 * 
	 * @param action
	 * @return
	 */
	public static Action getInverseAction(Action action) {
		switch (action) {
		case GO_DOWN:
			return Action.GO_UP;
		case GO_UP:
			return Action.GO_DOWN;
		case GO_LEFT:
			return Action.GO_RIGHT;
		case GO_RIGHT:
			return Action.GO_LEFT;
		default:
		case DO_NOTHING:
			return Action.DO_NOTHING;
		}
	}

}
