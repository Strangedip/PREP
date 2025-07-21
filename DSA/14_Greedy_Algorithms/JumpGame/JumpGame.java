// This problem is implemented in 10_Dynamic_Programming/JumpGame/JumpGame.java
// Jump Game is fundamentally a greedy algorithm problem, though it can also be solved with DP
// 
// The greedy solution for Jump Game I:
// - Track the maximum reachable position
// - If current position > max reachable, return false
// - Update max reachable = max(current_max, current_position + jump_length)
//
// The greedy solution for Jump Game II:
// - Use BFS-like approach: track current jump range and farthest reachable
// - When reaching end of current range, increment jumps and extend range
//
// Please refer to: 10_Dynamic_Programming/JumpGame/JumpGame.java for full implementation 