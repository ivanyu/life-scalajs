package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.{CellCoords, Neighbours}
import org.scalatest.{Matchers, FunSuite}

class CellPlaneSpec extends FunSuite with Matchers {
  private val bigPlane = CellPlane(10, 10)
  private val rightmost = bigPlane.width - 1
  private val lowermost = bigPlane.height - 1

  test("For 1x1 universe all the neighbours should be 0") {
    val plane = CellPlane(1, 1)
    plane.getNeighbours(CellCoords(0, 0)) shouldBe
      Neighbours(
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0),
        CellCoords(0, 0))
  }

  test("For big enough universe neighbours of central cell should be just shifted") {
    val row = 5
    val col = 5
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, row - 1),
        CellCoords(col, row - 1),
        CellCoords(col + 1, row - 1),
        CellCoords(col + 1, row),
        CellCoords(col + 1, row + 1),
        CellCoords(col, row + 1),
        CellCoords(col - 1, row + 1),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of upper-left corner cell should be properly calculated") {
    val row = 0
    val col = 0
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(rightmost, lowermost),
        CellCoords(col, lowermost),
        CellCoords(col + 1, lowermost),
        CellCoords(col + 1, row),
        CellCoords(col + 1, row + 1),
        CellCoords(col, row + 1),
        CellCoords(rightmost, row + 1),
        CellCoords(rightmost, row))
  }

  test("For big enough universe neighbours of upper border cell should be properly calculated") {
    val row = 0
    val col = bigPlane.width / 2
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, lowermost),
        CellCoords(col, lowermost),
        CellCoords(col + 1, lowermost),
        CellCoords(col + 1, row),
        CellCoords(col + 1, row + 1),
        CellCoords(col, row + 1),
        CellCoords(col - 1, row + 1),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of upper-right corner cell should be properly calculated") {
    val row = 0
    val col = rightmost
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, lowermost),
        CellCoords(col, lowermost),
        CellCoords(0, lowermost),
        CellCoords(0, row),
        CellCoords(0, row + 1),
        CellCoords(col, row + 1),
        CellCoords(col - 1, row + 1),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of right border cell should be properly calculated") {
    val row = bigPlane.height / 2
    val col = rightmost
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, row - 1),
        CellCoords(col, row - 1),
        CellCoords(0, row - 1),
        CellCoords(0, row),
        CellCoords(0, row + 1),
        CellCoords(col, row + 1),
        CellCoords(col - 1, row + 1),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of lower-right corner cell should be properly calculated") {
    val row = lowermost
    val col = rightmost
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, row - 1),
        CellCoords(col, row - 1),
        CellCoords(0, row - 1),
        CellCoords(0, row),
        CellCoords(0, 0),
        CellCoords(col, 0),
        CellCoords(col - 1, 0),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of lower border cell should be properly calculated") {
    val row = lowermost
    val col = bigPlane.width / 2
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(col - 1, row - 1),
        CellCoords(col, row - 1),
        CellCoords(col + 1, row - 1),
        CellCoords(col + 1, row),
        CellCoords(col + 1, 0),
        CellCoords(col, 0),
        CellCoords(col - 1, 0),
        CellCoords(col - 1, row))
  }

  test("For big enough universe neighbours of lower-left corner cell should be properly calculated") {
    val row = lowermost
    val col = 0
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(rightmost, row - 1),
        CellCoords(col, row - 1),
        CellCoords(col + 1, row - 1),
        CellCoords(col + 1, row),
        CellCoords(col + 1, 0),
        CellCoords(col, 0),
        CellCoords(rightmost, 0),
        CellCoords(rightmost, row))
  }

  test("For big enough universe neighbours of left border cell should be properly calculated") {
    val row = bigPlane.height / 2
    val col = 0
    bigPlane.getNeighbours(CellCoords(col, row)) shouldBe
      Neighbours(
        CellCoords(rightmost, row - 1),
        CellCoords(col, row - 1),
        CellCoords(col + 1, row - 1),
        CellCoords(col + 1, row),
        CellCoords(col + 1, row + 1),
        CellCoords(col, row + 1),
        CellCoords(rightmost, row + 1),
        CellCoords(rightmost, row))
  }
}
