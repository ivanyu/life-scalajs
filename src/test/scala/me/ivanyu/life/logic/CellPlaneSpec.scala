package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.{Coords, Neighbours}
import org.scalatest.{Matchers, FunSuite}

class CellPlaneSpec extends FunSuite with Matchers {
  private val bigPlane = CellPlane(10, 10)
  private val rightmost = bigPlane.width - 1
  private val lowermost = bigPlane.height - 1

  test("For 1x1 universe all the neighbours should be 0") {
    val plane = CellPlane(1, 1)
    plane.getNeighbours(0, 0) shouldBe
      Neighbours(
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0),
        Coords(0, 0))
  }

  test("For big enough universe neighbours of central cell should be just shifted") {
    val x = 5
    val y = 5
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, y - 1),
        Coords(x, y - 1),
        Coords(x + 1, y - 1),
        Coords(x + 1, y),
        Coords(x + 1, y + 1),
        Coords(x, y + 1),
        Coords(x - 1, y + 1),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of upper-left corner cell should be properly calculated") {
    val x = 0
    val y = 0
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(rightmost, lowermost),
        Coords(x, lowermost),
        Coords(x + 1, lowermost),
        Coords(x + 1, y),
        Coords(x + 1, y + 1),
        Coords(x, y + 1),
        Coords(rightmost, y + 1),
        Coords(rightmost, y))
  }

  test("For big enough universe neighbours of upper border cell should be properly calculated") {
    val x = bigPlane.width / 2
    val y = 0
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, lowermost),
        Coords(x, lowermost),
        Coords(x + 1, lowermost),
        Coords(x + 1, y),
        Coords(x + 1, y + 1),
        Coords(x, y + 1),
        Coords(x - 1, y + 1),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of upper-right corner cell should be properly calculated") {
    val x = rightmost
    val y = 0
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, lowermost),
        Coords(x, lowermost),
        Coords(0, lowermost),
        Coords(0, y),
        Coords(0, y + 1),
        Coords(x, y + 1),
        Coords(x - 1, y + 1),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of right border cell should be properly calculated") {
    val x = rightmost
    val y = bigPlane.height / 2
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, y - 1),
        Coords(x, y - 1),
        Coords(0, y - 1),
        Coords(0, y),
        Coords(0, y + 1),
        Coords(x, y + 1),
        Coords(x - 1, y + 1),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of lower-right corner cell should be properly calculated") {
    val x = rightmost
    val y = lowermost
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, y - 1),
        Coords(x, y - 1),
        Coords(0, y - 1),
        Coords(0, y),
        Coords(0, 0),
        Coords(x, 0),
        Coords(x - 1, 0),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of lower border cell should be properly calculated") {
    val x = bigPlane.width / 2
    val y = lowermost
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(x - 1, y - 1),
        Coords(x, y - 1),
        Coords(x + 1, y - 1),
        Coords(x + 1, y),
        Coords(x + 1, 0),
        Coords(x, 0),
        Coords(x - 1, 0),
        Coords(x - 1, y))
  }

  test("For big enough universe neighbours of lower-left corner cell should be properly calculated") {
    val x = 0
    val y = lowermost
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(rightmost, y - 1),
        Coords(x, y - 1),
        Coords(x + 1, y - 1),
        Coords(x + 1, y),
        Coords(x + 1, 0),
        Coords(x, 0),
        Coords(rightmost, 0),
        Coords(rightmost, y))
  }

  test("For big enough universe neighbours of left border cell should be properly calculated") {
    val x = 0
    val y = bigPlane.height / 2
    bigPlane.getNeighbours(x, y) shouldBe
      Neighbours(
        Coords(rightmost, y - 1),
        Coords(x, y - 1),
        Coords(x + 1, y - 1),
        Coords(x + 1, y),
        Coords(x + 1, y + 1),
        Coords(x, y + 1),
        Coords(rightmost, y + 1),
        Coords(rightmost, y))
  }
}
