# Gas Station

## Problem Statement
There are n gas stations along a circular route, where the amount of gas at the ith station is gas[i]. You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from the ith station to its next (i+1)th station. You begin the journey with an empty tank at one of the gas stations. Given two integer arrays gas and cost, return the starting gas station's index if you can travel around the circuit once in the clockwise direction, otherwise return -1.

## Example
```
Input: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
Output: 3
Explanation: 
Start at station 3 (index 3) and fill up with 4 units of gas.
Travel to station 4. tank = 0 + 4 - 1 = 3
Travel to station 0. tank = 3 + 5 - 2 = 6
Travel to station 1. tank = 6 + 1 - 3 = 4
Travel to station 2. tank = 4 + 2 - 4 = 2
Travel to station 3. The cost is 5 and tank = 2 + 3 - 5 = 0
You arrive at station 3, the starting point. You can complete the journey.
```

## Approach 1: Greedy Algorithm (Optimal!)

### Key Insights:
1. **If total gas < total cost**, impossible to complete circuit
2. **If solution exists**, it's unique
3. **Starting from any point**, if we can't reach a station, we can't start from any point before it

### How it works:
1. **Check if solution exists** (total gas >= total cost)
2. **Track current tank** and find where we run out of gas
3. **When tank becomes negative**, start from next station

### Key Logic:
```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0;      // Total gas - total cost
    int currentTank = 0;    // Current gas in tank
    int startStation = 0;   // Potential starting station
    
    for (int i = 0; i < gas.length; i++) {
        int net = gas[i] - cost[i];
        totalTank += net;
        currentTank += net;
        
        // If current tank goes negative, can't start from any previous station
        if (currentTank < 0) {
            startStation = i + 1;  // Try starting from next station
            currentTank = 0;       // Reset current tank
        }
    }
    
    // Return starting station if circuit is possible
    return totalTank >= 0 ? startStation : -1;
}
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through arrays
- **Space:** O(1) - Only using variables

## Approach 2: Brute Force (For Understanding)

### How it works:
1. **Try each station** as starting point
2. **Simulate the journey** from each starting point
3. **Return first valid** starting station

### Key Logic:
```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int n = gas.length;
    
    for (int start = 0; start < n; start++) {
        int tank = 0;
        boolean canComplete = true;
        
        // Try to complete circuit starting from 'start'
        for (int i = 0; i < n; i++) {
            int station = (start + i) % n;
            tank += gas[station] - cost[station];
            
            if (tank < 0) {
                canComplete = false;
                break;
            }
        }
        
        if (canComplete) {
            return start;
        }
    }
    
    return -1;
}
```

### Time & Space Complexity:
- **Time:** O(n²) - Try each starting point
- **Space:** O(1) - Only using variables

## Why Greedy Works:

### Mathematical Proof:
1. **If we can't reach station j from station i**, then we can't reach j from any station between i and j
2. **Proof:** If station k (i < k < j) could reach j, then combining paths i→k and k→j would allow i to reach j (contradiction)
3. **Therefore:** When we fail at station j, the next possible start is j+1

### Visualization:
```
gas  = [1, 2, 3, 4, 5]
cost = [3, 4, 5, 1, 2]
net  = [-2,-2,-2, 3, 3]

Station 0: tank = -2 (fail) → try station 1
Station 1: tank = -2-2 = -4 (fail) → try station 2  
Station 2: tank = -2 (fail) → try station 3
Station 3: tank = 3, then 3+3=6, then 6-2=4, then 4-2=2, then 2-2=0 ✓
```

## Edge Cases:
1. **Empty arrays** → Return -1
2. **Single station** → Check if gas[0] >= cost[0]
3. **All stations have deficit** → Return -1
4. **Exact balance** (total gas = total cost) → Find valid start

## Key Observations:
1. **Unique solution:** If circuit is possible, there's exactly one valid starting point
2. **Greedy choice:** Always optimal to start from the earliest possible station
3. **Total balance:** Sum of all (gas[i] - cost[i]) must be non-negative

## Alternative Perspective:

### Tank at each station:
```java
// Calculate net gain/loss at each station
int[] net = new int[n];
for (int i = 0; i < n; i++) {
    net[i] = gas[i] - cost[i];
}

// Find starting point where cumulative sum never goes negative
```

## LeetCode Similar Problems:
- [134. Gas Station](https://leetcode.com/problems/gas-station/) (this problem)
- [135. Candy](https://leetcode.com/problems/candy/)
- [122. Best Time to Buy and Sell Stock II](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/)
- [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/)

## Interview Tips:
- Start with brute force to show understanding
- Optimize to greedy with mathematical reasoning
- Explain why greedy choice is optimal
- Handle edge cases like single station
- This demonstrates classic greedy algorithm thinking 