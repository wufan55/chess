package team.IRobot;

import java.util.Random;

public interface IRobot {
        static final Random rand = new Random();

        /**
         * There we provide a default implementation to simulate robot's behavior
         *
         * @return a {@code robot.Pair} which contains a valid (x,y) position
         */
        default Pair getDeterminedPos() {
            return new Pair(rand.nextInt(15) + 1, rand.nextInt(15) + 1);
        }

        /**
         * This method is used to retrieve game board such that robot can determine its (x,y) position
         * @param gameBoard the 2-dimension array to represent the game board
         */
        void retrieveGameBoard(int[][] gameBoard);
    }
