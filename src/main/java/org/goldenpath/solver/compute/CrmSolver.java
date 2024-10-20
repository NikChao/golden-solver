package org.goldenpath.solver.compute;

import org.goldenpath.solver.compute.model.CrmInput;
import org.goldenpath.solver.compute.model.PlayerParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Counterfactual regret minimization solver
 */
public class CrmSolver {
    private static final int NUM_ACTIONS = 3; // Fold, Call, Raise

    private final HandResolver handResolver;

    public CrmSolver(HandResolver handResolver) {
        this.handResolver = handResolver;
    }

    public Map<String, Map<String, double[]>> solve(CrmInput input) {
        return solve(input, 50);
    }

    // The method that performs the CRM algorithm
    public Map<String, Map<String, double[]>> solve(CrmInput input, int iterations) {
        var oopPlayer = input.players()[0];
        var ipPlayer = input.players()[1];

        Map<String, Map<String, double[]>> strategyProfile = new HashMap<>();

        // For each hand in the player's range, we'll store regrets and strategies
        Map<String, double[]> oopHandRegrets = new HashMap<>();
        Map<String, double[]> ipHandRegrets = new HashMap<>();
        Map<String, double[]> oopHandStrategies = new HashMap<>();
        Map<String, double[]> ipHandStrategies = new HashMap<>();
        Map<String, double[]> oopHandStrategySum = new HashMap<>();
        Map<String, double[]> ipHandStrategySum = new HashMap<>();

        // Initialize hands and their corresponding data structures
        for (String hand : oopPlayer.range()) {
            oopHandRegrets.put(hand, new double[NUM_ACTIONS]);
            oopHandStrategies.put(hand, new double[NUM_ACTIONS]);
            oopHandStrategySum.put(hand, new double[NUM_ACTIONS]);
        }

        for (String hand : ipPlayer.range()) {
            ipHandRegrets.put(hand, new double[NUM_ACTIONS]);
            ipHandStrategies.put(hand, new double[NUM_ACTIONS]);
            ipHandStrategySum.put(hand, new double[NUM_ACTIONS]);
        }

        // CRM loop over iterations
        for (int iter = 0; iter < iterations; iter++) {
            // For each hand, we compute the strategy and simulate actions
            for (String oopHand : oopPlayer.range()) {
                double[] oopStrategy = getStrategy(oopHandRegrets.get(oopHand), oopHandStrategySum.get(oopHand));
                oopHandStrategies.put(oopHand, oopStrategy);
            }

            for (String ipHand : ipPlayer.range()) {
                double[] ipStrategy = getStrategy(ipHandRegrets.get(ipHand), ipHandStrategySum.get(ipHand));
                ipHandStrategies.put(ipHand, ipStrategy);
            }

            // Evaluate each possible action for both OOP and IP hands
            for (String oopHand : oopPlayer.range()) {
                for (String ipHand : ipPlayer.range()) {
                    double[] oopUtility = new double[NUM_ACTIONS];
                    double[] ipUtility = new double[NUM_ACTIONS];

                    // Simulate all action combinations: Fold (0), Call (1), Raise (2)
                    for (int oopAction = 0; oopAction < NUM_ACTIONS; oopAction++) {
                        for (int ipAction = 0; ipAction < NUM_ACTIONS; ipAction++) {
                            var oopOutcome = evaluateOutcome(
                                    input,
                                    oopPlayer,
                                    ipPlayer,
                                    oopAction,
                                    ipAction,
                                    oopHand,
                                    ipHand);
                            var ipOutcome = -oopOutcome;

                            oopUtility[oopAction] += oopOutcome * ipHandStrategies.get(ipHand)[ipAction];
                            ipUtility[ipAction] += ipOutcome * oopHandStrategies.get(oopHand)[oopAction];
                        }
                    }

                    // Calculate regrets for each action
                    updateRegrets(oopHandRegrets.get(oopHand), oopUtility, oopHandStrategies.get(oopHand));
                    updateRegrets(ipHandRegrets.get(ipHand), ipUtility, ipHandStrategies.get(ipHand));
                }
            }
        }

        // Normalize strategy sums to get final strategy profile
        strategyProfile.put("oop", new HashMap<>());
        strategyProfile.put("ip", new HashMap<>());

        for (String oopHand : oopPlayer.range()) {
            strategyProfile.get("oop").put(oopHand, normalize(oopHandStrategySum.get(oopHand)));
        }

        for (String ipHand : ipPlayer.range()) {
            strategyProfile.get("ip").put(ipHand, normalize(ipHandStrategySum.get(ipHand)));
        }

        return strategyProfile;
    }

    // Get strategy based on regret sums
    private double[] getStrategy(double[] regrets, double[] strategySum) {
        double[] strategy = new double[NUM_ACTIONS];
        double normalizingSum = 0;

        for (int i = 0; i < NUM_ACTIONS; i++) {
            strategy[i] = Math.max(regrets[i], 0);
            normalizingSum += strategy[i];
        }

        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (normalizingSum > 0) {
                strategy[i] /= normalizingSum;
            } else {
                strategy[i] = 1.0 / NUM_ACTIONS;
            }
            strategySum[i] += strategy[i]; // Track cumulative strategy
        }

        return strategy;
    }

    // Calculate expected value of a strategy
    private double getExpectedValue(double[] utility, double[] strategy) {
        double expectedValue = 0;
        for (int i = 0; i < NUM_ACTIONS; i++) {
            expectedValue += strategy[i] * utility[i];
        }
        return expectedValue;
    }

    // Normalize a strategy sum to get a final mixed strategy
    private double[] normalize(double[] strategySum) {
        double[] normalizedStrategy = new double[NUM_ACTIONS];
        double total = 0;
        for (double value : strategySum) {
            total += value;
        }

        for (int i = 0; i < NUM_ACTIONS; i++) {
            if (total > 0) {
                normalizedStrategy[i] = strategySum[i] / total;
            } else {
                normalizedStrategy[i] = 1.0 / NUM_ACTIONS;
            }
        }
        return normalizedStrategy;
    }

    // Function to evaluate the outcome based on player actions and their hands
    private double evaluateOutcome(CrmInput input, PlayerParams oopPlayer, PlayerParams ipPlayer, int oopAction, int ipAction, String oopHand, String ipHand) {
        var pot = input.pot();
        var oopBetSize = getBetSize(oopAction, oopPlayer.flopBetSizes());
        var ipBetSize = getBetSize(ipAction, ipPlayer.flopBetSizes());

        // Example logic for action resolution based on bets
        if (oopAction == 0) { // OOP folds
            return -pot / 2.0;
        } else if (oopAction == 1 && ipAction == 0) { // OOP calls, IP folds
            return pot / 2.0;
        } else if (oopAction == 1 && ipAction == 1) { // Both call
            return simulateShowdown(toHand(oopHand), toHand(ipHand), input.boardCards(), pot);
        } else if (oopAction == 2 || ipAction == 2) { // Raise involved
            return resolveBetting(input, oopPlayer, ipPlayer, oopBetSize, ipBetSize);
        }
        return 0; // Default return in case no action resolves
    }

    // Simulate a showdown and determine the outcome
    private double simulateShowdown(String[] oopHand, String[] ipHand, String[] boardCards, int pot) {
        // Use external checkHands function to determine who wins
        var result = handResolver.compareHands(oopHand, ipHand, boardCards);
        if (result.equals(HandResolver.Player.OutOfPosition)) {
            return pot;
        } else if (result.equals(HandResolver.Player.InPosition)) {
            return -pot;
        } else {
            return 0;
        }
    }

    // Resolve the outcome for betting/raising scenarios
    private double resolveBetting(CrmInput input, PlayerParams oopPlayer, PlayerParams ipPlayer, double oopBetSize, double ipBetSize) {
        double pot = input.pot();
        // Placeholder for bet resolution logic
        return pot + oopBetSize + ipBetSize;
    }

    // Get the bet size based on action (Fold = 0, Call = 1, Raise = 2)
    private double getBetSize(int action, double[] betSizes) {
        if (action == 2) {
            return betSizes[0]; // Use the first raise size
        }
        return 0;
    }

    // Update regrets based on utility and strategy
    private void updateRegrets(double[] regrets, double[] utility, double[] strategy) {
        double expectedValue = getExpectedValue(utility, strategy);
        for (int i = 0; i < NUM_ACTIONS; i++) {
            regrets[i] += utility[i] - expectedValue;
        }
    }

    // Pick a random hand from the player's range
    private String[] chooseRandomHand(String[] range, String[] alreadyPlayedCards) {
        var random = new Random();

        // TODO: dedupe using already played cards
        var randomHand = range[random.nextInt(range.length)];
        return toHand(randomHand);
    }

    private boolean contains(String[] cards, String card) {
        for (var i = 0; i < cards.length; i++) {
            if (card.equals(cards[i])) {
                return true;
            }
        }

        return false;
    }

    private static String[] toHand(String hand) {
        var firstHand = "" + hand.charAt(0) + hand.charAt(1);
        var secondHand = "" + hand.charAt(2) + hand.charAt(3);
        return new String[]{firstHand, secondHand};
    }
}
