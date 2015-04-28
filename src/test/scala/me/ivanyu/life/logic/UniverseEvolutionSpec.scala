package me.ivanyu.life.logic

import org.scalatest.{FunSuite, Matchers}

class UniverseEvolutionSpec extends FunSuite with Matchers {
  /* Still */
  test("Empty") {
    val universe = Universe(10, 10)
    universe.nextState._1 shouldBe universe
  }

  test("Block") {
    val universe = Universe(4, 4)
      .setAlive(1, 1)
      .setAlive(1, 2)
      .setAlive(2, 1)
      .setAlive(2, 2)
    testStill(universe)
  }

  test("Block corner") {
    val universe = Universe(4, 4)
      .setAlive(0, 0)
      .setAlive(0, 3)
      .setAlive(3, 0)
      .setAlive(3, 3)
    testStill(universe)
  }

  test("Beehive") {
    val universe = Universe(6, 5)
      .setAlive(2, 1)
      .setAlive(3, 1)
      .setAlive(1, 2)
      .setAlive(4, 2)
      .setAlive(2, 3)
      .setAlive(3, 3)
    testStill(universe)
  }

  test("Loaf") {
    val universe = Universe(6, 6)
      .setAlive(2, 1)
      .setAlive(3, 1)
      .setAlive(1, 2)
      .setAlive(4, 2)
      .setAlive(2, 3)
      .setAlive(4, 3)
      .setAlive(3, 4)
    testStill(universe)
  }

  test("Boat") {
    val universe = Universe(5, 5)
      .setAlive(1, 1)
      .setAlive(2, 1)
      .setAlive(1, 2)
      .setAlive(3, 2)
      .setAlive(2, 3)
    testStill(universe)
  }

  /* Oscillators */
  test("Blinker") {
    val universe1 = Universe(5, 5)
      .setAlive(2, 1)
      .setAlive(2, 2)
      .setAlive(2, 3)
    val universe2 = Universe(5, 5)
      .setAlive(1, 2)
      .setAlive(2, 2)
      .setAlive(3, 2)
    testOscillatorPeriod2(universe1, universe2)
  }

  test("Blinker corner") {
    val universe1 = Universe(5, 5)
      .setAlive(0, 0)
      .setAlive(1, 0)
      .setAlive(4, 0)
    val universe2 = Universe(5, 5)
      .setAlive(0, 0)
      .setAlive(0, 1)
      .setAlive(0, 4)
    testOscillatorPeriod2(universe1, universe2)
  }

  test("Toad") {
    val universe1 = Universe(6, 6)
      .setAlive(2, 2)
      .setAlive(3, 2)
      .setAlive(4, 2)
      .setAlive(1, 3)
      .setAlive(2, 3)
      .setAlive(3, 3)
    val universe2 = Universe(6, 6)
      .setAlive(3, 1)
      .setAlive(1, 2)
      .setAlive(4, 2)
      .setAlive(1, 3)
      .setAlive(4, 3)
      .setAlive(2, 4)
    testOscillatorPeriod2(universe1, universe2)
  }

  test("Beacon") {
    val universe1 = Universe(6, 6)
      .setAlive(1, 1)
      .setAlive(2, 1)
      .setAlive(1, 2)
      .setAlive(2, 2)
      .setAlive(3, 3)
      .setAlive(4, 3)
      .setAlive(3, 4)
      .setAlive(4, 4)
    val universe2 = Universe(6, 6)
      .setAlive(1, 1)
      .setAlive(2, 1)
      .setAlive(1, 2)
      .setAlive(4, 3)
      .setAlive(3, 4)
      .setAlive(4, 4)
    testOscillatorPeriod2(universe1, universe2)
  }

  test("Beacon corner") {
    val universe1 = Universe(6, 6)
      .setAlive(4, 4)
      .setAlive(4, 5)
      .setAlive(5, 4)
      .setAlive(5, 5)
      .setAlive(0, 0)
      .setAlive(0, 1)
      .setAlive(1, 0)
      .setAlive(1, 1)
    val universe2 = Universe(6, 6)
      .setAlive(4, 4)
      .setAlive(4, 5)
      .setAlive(5, 4)
      .setAlive(0, 1)
      .setAlive(1, 0)
      .setAlive(1, 1)
    testOscillatorPeriod2(universe1, universe2)
  }

  private def testStill(u: Universe): Unit = {
    u.nextState._1 shouldBe u
  }

  private def testOscillatorPeriod2(u1: Universe, u2: Universe): Unit = {
    u1.nextState._1 shouldBe u2
    u1.nextState._1.nextState._1 shouldBe u1
    u2.nextState._1 shouldBe u1
    u2.nextState._1.nextState._1 shouldBe u2
  }
}
