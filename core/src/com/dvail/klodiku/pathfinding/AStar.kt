package com.dvail.klodiku.pathfinding

import java.util.*

/**
 * Created by dave on 1/17/15.
 */
object AStar {

    fun manhattanDistance(a: Node, b: Node): Int {

        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y)

    }

    fun getNeighbors(grid: Array<IntArray>, parent: Node, goal: Node): List<Node> {
        val neighbors = ArrayList<Node>(4)

        val xLeft = parent.x - 1
        val xRight = parent.x + 1
        val yUp = parent.y - 1
        val yDown = parent.y + 1

        // Check if each potential neighbor is within the grid bounds, and does not have a value < 0 (impassable)
        if (xLeft > -1 && grid[xLeft][parent.y] > -1) {
            neighbors.add(Node(xLeft, parent.y, grid[xLeft][parent.y], parent, goal))
        }

        if (xRight < grid.size && grid[xRight][parent.y] > -1) {
            neighbors.add(Node(xRight, parent.y, grid[xRight][parent.y], parent, goal))
        }

        if (yUp > -1 && grid[parent.x][yUp] > -1) {
            neighbors.add(Node(parent.x, yUp, grid[parent.x][yUp], parent, goal))
        }

        if (yDown < grid[0].size && grid[parent.x][yDown] > -1) {
            neighbors.add(Node(parent.x, yDown, grid[parent.x][yDown], parent, goal))
        }

        return neighbors
    }

    fun findPath(grid: Array<IntArray>, start: Node, goal: Node): List<Node> {

        val comparator = object : Comparator<Node> {

            override fun compare(o: Node, t1: Node): Int {
                val comparatorValue: Int

                if (o.cost < t1.cost) {
                    comparatorValue = -1
                } else if (o.cost > t1.cost) {
                    comparatorValue = 1
                } else {
                    comparatorValue = 0
                }

                return comparatorValue
            }

            override fun equals(o: Any?): Boolean {

                return false
            }
        }

        var currentNode: Node
        var neighbors: List<Node>
        val openNodes = PriorityQueue(5, comparator)
        val closedNodes = HashSet<Node>()

        openNodes.add(start)

        while (!openNodes.isEmpty()) {

            currentNode = openNodes.poll()
            closedNodes.add(currentNode)

            neighbors = getNeighbors(grid, currentNode, goal)

            for (node in neighbors) {

                if (openNodes.contains(node)) {

                    // TODO Update cost value here for more efficient routes

                } else if (!closedNodes.contains(node)) {

                    openNodes.add(node)
                }

            }

            if (currentNode == goal) {

                val path = ArrayList<Node>()

                while (currentNode.parent != null) {
                    path.add(currentNode)
                    currentNode = currentNode.parent!!
                }

                return path
            }

        }

        return arrayListOf(start) // No path, return the starting node
    }

    class Node(var x: Int, var y: Int) {
        var cost: Int = 0
        var parent: Node? = null

        init {
            this.cost = 0
            this.parent = null
        }

        constructor(x: Int, y: Int, cost: Int, parent: Node, goal: Node) : this(x, y) {
            this.cost = cost + parent.cost + manhattanDistance(this, goal)
            this.parent = parent
        }

        override fun equals(o: Any?): Boolean {

            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            val node = o as Node?

            return x == node?.x && y == node?.y

        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }
    }

}