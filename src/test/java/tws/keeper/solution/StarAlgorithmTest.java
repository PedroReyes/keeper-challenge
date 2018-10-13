package tws.keeper.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tws.keeper.model.Action.GO_DOWN;
import static tws.keeper.model.Action.GO_LEFT;
import static tws.keeper.model.Action.GO_RIGHT;
import static tws.keeper.model.Action.GO_UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import tws.keeper.model.Action;
import tws.keeper.model.Cell;
import tws.keeper.model.Observable;
import tws.keeper.model.Position;

class StarAlgorithmTest {

	private static final int P = Cell.PATH.ordinal();
	private static final int W = Cell.WALL.ordinal();

	// @formatter:off
	private static final int[][] MAP_WITHOUT_OBSTACLES = new int[][] { 
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P}};

	private static final int[][] MAP_WITH_OBSTACLES = new int[][] { 
			{P, P, P, P, P, P, P, P, P, P},
			{W, P, P, P, P, P, P, P, P, P},
			{P, W, P, P, P, P, P, P, P, P},
			{P, P, W, W, P, P, P, P, P, P},
			{P, P, P, P, P, W, P, W, W, W},
			{P, P, P, P, P, W, W, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, W, P, P, P, P, P},
			{P, P, P, P, W, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P}};
				
	private static final int[][] MAP_WITH_NO_POSSIBLE_PATH = new int[][] { 
			{P, P, P, W, P, P, P, P, P, P},
			{P, P, P, W, P, P, P, P, P, P},
			{P, P, P, W, P, P, P, P, P, P},
			{W, W, W, W, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P},
			{P, P, P, P, P, P, P, P, P, P}};

	// @formatter:on

	private static final Position KEEPER_POSITION = new Position(0, 0);
	private static final Position DOOR_POSITION = new Position(MAP_WITHOUT_OBSTACLES[0].length - 1,
			MAP_WITHOUT_OBSTACLES.length - 1);

	private static final String ERROR_FOR_NO_PATH_POSSIBLE = "No path to the target was found";

	private static final List<Action> availableActions = Arrays.asList(GO_DOWN, GO_LEFT, GO_RIGHT, GO_UP);

	private ExploratoryAlgorithm observableUtils;

	@Mock
	private Observable maze;

	@Test
	void testCreatePathForMapWithNoObstacles() {
		List<Action> path = createStarAlgorithm(MAP_WITHOUT_OBSTACLES).createPath();

		assertEquals(DOOR_POSITION, lastPathPosition(path, KEEPER_POSITION));
	}

	private StarAlgorithm createStarAlgorithm(int[][] specificMap) {
		MockitoAnnotations.initMocks(this);
		Mockito.when(maze.getKeeperPosition()).thenReturn(KEEPER_POSITION);

		observableUtils = Mockito.spy(new ExploratoryAlgorithm(maze, availableActions));
		Mockito.when(observableUtils.getDoorPosition()).thenReturn(DOOR_POSITION);
		Mockito.when(observableUtils.getAvailableActions()).thenReturn(availableActions);
		Mockito.when(observableUtils.getVisitedPositions()).thenReturn(createMap(specificMap));

		return new StarAlgorithm(observableUtils);
	}

	@Test
	void testCreatePathForMapWithObstacles() {
		List<Action> path = createStarAlgorithm(MAP_WITH_OBSTACLES).createPath();

		assertEquals(DOOR_POSITION, lastPathPosition(path, KEEPER_POSITION));
	}

	@Test
	void testCreatePathForMapWithNoTraversalPathToDoorPosition() {
		Executable closureContainingCodeToTest = () -> createStarAlgorithm(MAP_WITH_NO_POSSIBLE_PATH).createPath();

		assertThrows(IllegalStateException.class, closureContainingCodeToTest, ERROR_FOR_NO_PATH_POSSIBLE);
	}

	private static List<Position> createMap(int[][] map) {
		List<Position> mapPositions = new ArrayList<>();
		for (int i = 0; i < map[0].length; i++) {
			for (int j = 0; j < map.length; j++) {
				if (map[i][j] == P) {
					mapPositions.add(new Position(i, j));
				}
			}
		}

		return mapPositions;
	}

	private Position lastPathPosition(List<Action> path, Position initialPosition) {
		Position lastPosition = initialPosition;
		while (!path.isEmpty()) {
			Action action = path.get(0);
			lastPosition = MazeUtils.positionAfterAction(action, lastPosition);
			path.remove(0);
		}
		return lastPosition;
	}

}
