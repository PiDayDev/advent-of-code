package common

import java.util.*

interface CandidateTree<Solution> {
    fun candidateNodes(): Collection<CandidateTree<Solution>>
    fun isSingleCandidate(): Boolean
    fun candidate(): Solution
}

class BranchAndBound<Problem, Solution>(
    val heuristicSolve: (Problem) -> Solution,
    val populateCandidates: (Problem) -> Deque<CandidateTree<Solution>>
) {

    // Implementation of branch and bound,
    // assuming the objective function f is to be minimized
    fun solve(
        problem: Problem,
        objective: (Solution) -> Long /*f*/,
        lowerBound: (CandidateTree<Solution>) -> Long /*bound*/
    ): Solution {
        val heuristicSolution = heuristicSolve(problem) // x_h
        var problemUpperBound = objective(heuristicSolution) // B = f(x_h)
        var currentOptimum = heuristicSolution
        // problem-specific queue initialization
        val candidateQueue = populateCandidates(problem)
        while (candidateQueue.isNotEmpty()) {
            val node = candidateQueue.pop()
            if (node.isSingleCandidate()) {
                val candidate = node.candidate()
                if (objective(candidate) < problemUpperBound) {
                    currentOptimum = candidate
//                    println("currentOptimum $currentOptimum")
                    problemUpperBound = objective(currentOptimum)
                }
                // else, node is a single candidate which is not optimum
            } else {
                // Node represents a branch of candidate solutions
                for (childBranch in node.candidateNodes()) {
                    if (lowerBound(childBranch) <= problemUpperBound) {
                        candidateQueue.push(childBranch)
                    }
                    // otherwise, bound(N_i) > B so we prune the branch
                }
            }
        }
        return currentOptimum
    }
}
