package tws.keeper.solution;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tws.keeper.model.Action.GO_DOWN;
import static tws.keeper.model.Action.GO_LEFT;
import static tws.keeper.model.Action.GO_RIGHT;
import static tws.keeper.model.Action.GO_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import tws.keeper.model.Action;
import tws.keeper.model.Cell;
import tws.keeper.model.Observable;
import tws.keeper.model.Position;

class MazeUtilsTest {

	private static final List<Action> availableActions = Arrays.asList(GO_DOWN, GO_LEFT, GO_RIGHT, GO_UP);

	private static final Position KEEPER_POSITION = new Position(1, 1);
	private static final Position DOOR_POSITION = new Position(2, 1);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Observable maze;

	@Test
	void testActionForGoing() {
		// @formatter:off
		assertEquals(Action.GO_LEFT, MazeUtils.actionForGoing(new Position(2, 2), new Position(2, 1), availableActions));
		assertEquals(Action.GO_DOWN, MazeUtils.actionForGoing(new Position(2, 2), new Position(3, 2), availableActions));
		assertEquals(Action.GO_UP, MazeUtils.actionForGoing(new Position(2, 2), new Position(1, 2), availableActions));
		assertEquals(Action.GO_RIGHT, MazeUtils.actionForGoing(new Position(2, 2), new Position(2, 3), availableActions));
		// @formatter:on
	}

	@Test
	void testNoActionForGoing() {
		Assertions.assertThrows(IllegalStateException.class,
				() -> MazeUtils.actionForGoing(new Position(2, 2), new Position(4, 4), availableActions));
	}

	@Test
	void testPositionAfterAction() {
		assertEquals(new Position(0, 1), MazeUtils.positionAfterAction(Action.GO_UP, KEEPER_POSITION));
		assertEquals(new Position(1, 2), MazeUtils.positionAfterAction(Action.GO_RIGHT, KEEPER_POSITION));
		assertEquals(new Position(1, 0), MazeUtils.positionAfterAction(Action.GO_LEFT, KEEPER_POSITION));
		assertEquals(new Position(2, 1), MazeUtils.positionAfterAction(Action.GO_DOWN, KEEPER_POSITION));
		assertEquals(KEEPER_POSITION, MazeUtils.positionAfterAction(Action.DO_NOTHING, KEEPER_POSITION));
	}

	@Test
	void testHighestProbableActionDoor() {
		createMaze();

		assertEquals(Action.GO_RIGHT, MazeUtils.highestProbableAction(maze, availableActions, DOOR_POSITION));
	}

	@Test
	void testHighestProbableActionKey() {
		createMaze();

		assertEquals(Action.GO_DOWN, MazeUtils.highestProbableAction(maze, availableActions, null));
	}

	@Test
	void testPossibleActions() {
		createMaze();

		List<Action> expectedActions = new ArrayList<>();
		expectedActions.add(Action.GO_DOWN);
		expectedActions.add(Action.GO_RIGHT);
		expectedActions.add(Action.GO_LEFT);

		List<Action> possibleActions = MazeUtils.possibleActions(maze, availableActions);

		assertEquals(expectedActions.size(), possibleActions.size());
		assertTrue(expectedActions.containsAll(possibleActions));
	}

	@Test
	void testGetDoorPositionPossible() {
		createMaze();

		assertEquals(DOOR_POSITION, MazeUtils.getDoorPosition(maze, availableActions));
	}

	@Test
	void testGetDoorPositionNotPossible() {
		createMaze();

		Mockito.when(maze.lookDown()).thenReturn(Cell.PATH);

		assertEquals(null, MazeUtils.getDoorPosition(maze, availableActions));
	}

	@Test
	void testPossibleNextPositions() {
		createMaze();

		List<Action> expectedActions = new ArrayList<>();
		expectedActions.add(Action.GO_DOWN);
		expectedActions.add(Action.GO_RIGHT);
		expectedActions.add(Action.GO_LEFT);

		List<Position> possibleActions = MazeUtils.possibleNextPositions(maze, availableActions, DOOR_POSITION);

		assertEquals(expectedActions.size(), possibleActions.size());
		assertTrue(expectedActions.containsAll(possibleActions));
	}

	private void createMaze() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(maze.lookUp()).thenReturn(Cell.WALL);
		Mockito.when(maze.lookDown()).thenReturn(Cell.DOOR);
		Mockito.when(maze.lookRight()).thenReturn(Cell.KEY);
		Mockito.when(maze.lookLeft()).thenReturn(Cell.PATH);
		Mockito.when(maze.getKeysFound()).thenReturn(4);
		Mockito.when(maze.getTotalNumberOfKeys()).thenReturn(7);

		Mockito.when(maze.getKeeperPosition()).thenReturn(KEEPER_POSITION);
	}

}
