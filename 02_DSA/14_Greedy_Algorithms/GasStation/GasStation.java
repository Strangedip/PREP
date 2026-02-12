/**
 * Gas Station Problem
 * 
 * There are n gas stations along a circular route, where the amount of gas at 
 * the ith station is gas[i]. You have a car with an unlimited gas tank and it 
 * costs cost[i] of gas to travel from the ith station to its next (i + 1)th station.
 * 
 * You begin the journey with an empty tank at one of the gas stations.
 * 
 * Given two integer arrays gas and cost, return the starting gas station's index 
 * if you can travel around the circuit once in the clockwise direction, 
 * otherwise return -1.
 * 
 * If there exists a solution, it is guaranteed to be unique.
 * 
 * Example:
 * Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
 * Output: 3
 * Explanation: Start at station 3 (index 3) and fill up with 4 unit of gas.
 * Your tank = 0 + 4 = 4. Travel to station 4. Your tank = 4 - 1 + 5 = 8.
 * Travel to station 0. Your tank = 8 - 2 + 1 = 7. Travel to station 1.
 * Your tank = 7 - 3 + 2 = 6. Travel to station 2. Your tank = 6 - 4 + 3 = 5.
 * Travel to station 3. The cost is 5. Your gas is just enough to travel back to station 3.
 * Therefore, return 3 as the starting index.
 */
public class GasStation {
    
    /**
     * APPROACH 1: GREEDY ALGORITHM (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Key insight: If total gas >= total cost, solution exists and is unique.
     * Start from any station where we can't reach next station and try next station.
     */
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }
        
        int totalGas = 0;
        int totalCost = 0;
        int currentGas = 0;
        int startStation = 0;
        
        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
            currentGas += gas[i] - cost[i];
            
            // If we can't reach next station, try starting from next station
            if (currentGas < 0) {
                startStation = i + 1;
                currentGas = 0;
            }
        }
        
        // If total gas < total cost, no solution exists
        return totalGas >= totalCost ? startStation : -1;
    }
    
    /**
     * APPROACH 2: BRUTE FORCE (FOR COMPARISON)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * Try each station as starting point and simulate the journey.
     */
    public int canCompleteCircuitBruteForce(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }
        
        int n = gas.length;
        
        for (int start = 0; start < n; start++) {
            if (canCompleteFromStation(gas, cost, start)) {
                return start;
            }
        }
        
        return -1;
    }
    
    private boolean canCompleteFromStation(int[] gas, int[] cost, int start) {
        int n = gas.length;
        int currentGas = 0;
        
        for (int i = 0; i < n; i++) {
            int station = (start + i) % n;
            currentGas += gas[station] - cost[station];
            
            if (currentGas < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 3: TWO POINTERS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Alternative greedy approach using two pointers.
     */
    public int canCompleteCircuitTwoPointers(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }
        
        int n = gas.length;
        int start = 0;
        int end = 1;
        int currentGas = gas[start] - cost[start];
        
        // If there's only one station
        if (n == 1) {
            return currentGas >= 0 ? 0 : -1;
        }
        
        while (start != end || currentGas < 0) {
            // If current gas is negative, remove stations from start
            while (currentGas < 0 && start != end) {
                currentGas -= gas[start] - cost[start];
                start = (start + 1) % n;
                
                // If start becomes equal to end, no solution
                if (start == 0) {
                    return -1;
                }
            }
            
            // Add stations from end
            currentGas += gas[end] - cost[end];
            end = (end + 1) % n;
        }
        
        return start;
    }
    
    /**
     * APPROACH 4: KADANE'S ALGORITHM VARIANT
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Treat the problem similar to maximum subarray sum.
     */
    public int canCompleteCircuitKadane(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }
        
        int totalSum = 0;
        int currentSum = 0;
        int minSum = 0;
        int startIndex = 0;
        
        for (int i = 0; i < gas.length; i++) {
            int diff = gas[i] - cost[i];
            totalSum += diff;
            currentSum += diff;
            
            if (currentSum < minSum) {
                minSum = currentSum;
                startIndex = i + 1;
            }
        }
        
        return totalSum >= 0 ? startIndex % gas.length : -1;
    }
    
    /**
     * EXTENSION: MINIMUM FUEL NEEDED
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Find minimum initial fuel needed to complete the circuit from station 0.
     */
    public int minimumFuelNeeded(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return -1;
        }
        
        int currentFuel = 0;
        int minFuelNeeded = 0;
        
        for (int i = 0; i < gas.length; i++) {
            currentFuel += gas[i] - cost[i];
            minFuelNeeded = Math.min(minFuelNeeded, currentFuel);
        }
        
        // If total gas >= total cost, we need at least -minFuelNeeded initial fuel
        return currentFuel >= 0 ? Math.max(0, -minFuelNeeded) : -1;
    }
    
    /**
     * EXTENSION: ALL POSSIBLE STARTING STATIONS
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Find all stations from which we can complete the circuit.
     */
    public java.util.List<Integer> allPossibleStartStations(int[] gas, int[] cost) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        
        if (gas == null || cost == null || gas.length != cost.length) {
            return result;
        }
        
        // Check if solution exists at all
        int totalGas = 0, totalCost = 0;
        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
        }
        
        if (totalGas < totalCost) {
            return result; // No solution exists
        }
        
        // Find all valid starting points
        for (int start = 0; start < gas.length; start++) {
            if (canCompleteFromStation(gas, cost, start)) {
                result.add(start);
            }
        }
        
        return result;
    }
    
    /**
     * EXTENSION: CIRCULAR ARRAY WITH MAXIMUM SEGMENTS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Find the starting position that maximizes the number of stations we can visit.
     */
    public int maxStationsReachable(int[] gas, int[] cost) {
        if (gas == null || cost == null || gas.length != cost.length) {
            return 0;
        }
        
        int n = gas.length;
        int maxStations = 0;
        int bestStart = 0;
        
        for (int start = 0; start < n; start++) {
            int currentGas = 0;
            int stationsVisited = 0;
            
            for (int i = 0; i < n; i++) {
                int station = (start + i) % n;
                currentGas += gas[station] - cost[station];
                
                if (currentGas >= 0) {
                    stationsVisited++;
                } else {
                    break;
                }
            }
            
            if (stationsVisited > maxStations) {
                maxStations = stationsVisited;
                bestStart = start;
            }
        }
        
        return bestStart;
    }
    
    /**
     * Utility method to print arrays
     */
    private void printArrays(int[] gas, int[] cost, String label) {
        System.out.println(label + ":");
        System.out.print("Gas:  ");
        for (int g : gas) System.out.print(g + " ");
        System.out.println();
        System.out.print("Cost: ");
        for (int c : cost) System.out.print(c + " ");
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        GasStation solution = new GasStation();
        
        // Test Case 1: Standard example with solution
        System.out.println("=== Test Case 1: Standard Example ===");
        int[] gas1 = {1, 2, 3, 4, 5};
        int[] cost1 = {3, 4, 5, 1, 2};
        solution.printArrays(gas1, cost1, "Arrays");
        
        System.out.println("Greedy: " + solution.canCompleteCircuit(gas1, cost1));
        System.out.println("Brute Force: " + solution.canCompleteCircuitBruteForce(gas1, cost1));
        System.out.println("Two Pointers: " + solution.canCompleteCircuitTwoPointers(gas1, cost1));
        System.out.println("Kadane Variant: " + solution.canCompleteCircuitKadane(gas1, cost1));
        System.out.println("Min Fuel Needed: " + solution.minimumFuelNeeded(gas1, cost1));
        System.out.println();
        
        // Test Case 2: No solution
        System.out.println("=== Test Case 2: No Solution ===");
        int[] gas2 = {2, 3, 4};
        int[] cost2 = {3, 4, 3};
        solution.printArrays(gas2, cost2, "Arrays");
        
        System.out.println("Greedy: " + solution.canCompleteCircuit(gas2, cost2));
        System.out.println("Min Fuel Needed: " + solution.minimumFuelNeeded(gas2, cost2));
        System.out.println();
        
        // Test Case 3: Single station
        System.out.println("=== Test Case 3: Single Station ===");
        int[] gas3 = {5};
        int[] cost3 = {4};
        solution.printArrays(gas3, cost3, "Arrays");
        
        System.out.println("Greedy: " + solution.canCompleteCircuit(gas3, cost3));
        System.out.println("Two Pointers: " + solution.canCompleteCircuitTwoPointers(gas3, cost3));
        System.out.println();
        
        // Test Case 4: Multiple solutions possible
        System.out.println("=== Test Case 4: All Possible Starts ===");
        int[] gas4 = {3, 3, 4};
        int[] cost4 = {3, 4, 2};
        solution.printArrays(gas4, cost4, "Arrays");
        
        System.out.println("One solution: " + solution.canCompleteCircuit(gas4, cost4));
        System.out.println("All solutions: " + solution.allPossibleStartStations(gas4, cost4));
        System.out.println("Max reachable start: " + solution.maxStationsReachable(gas4, cost4));
        System.out.println();
        
        // Test Case 5: Edge case - exact match
        System.out.println("=== Test Case 5: Exact Match ===");
        int[] gas5 = {2, 4, 3, 1};
        int[] cost5 = {3, 2, 4, 1};
        solution.printArrays(gas5, cost5, "Arrays");
        
        System.out.println("Greedy: " + solution.canCompleteCircuit(gas5, cost5));
        System.out.println("Kadane Variant: " + solution.canCompleteCircuitKadane(gas5, cost5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(GasStation solution) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            // Generate test arrays
            int[] gas = new int[size];
            int[] cost = new int[size];
            java.util.Random rand = new java.util.Random(42);
            
            for (int i = 0; i < size; i++) {
                gas[i] = rand.nextInt(100) + 1;
                cost[i] = rand.nextInt(100) + 1;
            }
            
            // Ensure solution exists by adjusting total
            int totalGas = java.util.Arrays.stream(gas).sum();
            int totalCost = java.util.Arrays.stream(cost).sum();
            if (totalGas < totalCost) {
                gas[0] += (totalCost - totalGas + 1);
            }
            
            System.out.println("Array size: " + size);
            
            long startTime, endTime;
            
            // Greedy approach
            startTime = System.nanoTime();
            int result1 = solution.canCompleteCircuit(gas, cost);
            endTime = System.nanoTime();
            System.out.println("Greedy O(n): " + result1 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Two Pointers approach
            startTime = System.nanoTime();
            int result2 = solution.canCompleteCircuitTwoPointers(gas, cost);
            endTime = System.nanoTime();
            System.out.println("Two Pointers O(n): " + result2 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Kadane variant
            startTime = System.nanoTime();
            int result3 = solution.canCompleteCircuitKadane(gas, cost);
            endTime = System.nanoTime();
            System.out.println("Kadane Variant O(n): " + result3 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // For smaller arrays, test brute force
            if (size <= 1000) {
                startTime = System.nanoTime();
                int result4 = solution.canCompleteCircuitBruteForce(gas, cost);
                endTime = System.nanoTime();
                System.out.println("Brute Force O(n²): " + result4 + " (" + 
                                 (endTime - startTime) / 1_000_000.0 + " ms)");
                
                System.out.println("All results match: " + 
                                 (result1 == result2 && result2 == result3 && result3 == result4));
            } else {
                System.out.println("Results match: " + (result1 == result2 && result2 == result3));
            }
            
            System.out.println();
        }
    }
} 